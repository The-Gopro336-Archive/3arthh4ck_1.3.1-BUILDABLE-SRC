package me.earth.earthhack.impl.modules.combat.antisurround;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerObby
extends ObbyListener<AntiSurround> {
    private BlockPos crystalPos = null;

    public ListenerObby(AntiSurround module) {
        super(module, 10);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(MotionUpdateEvent event) {
        if (!((AntiSurround)this.module).async.getValue().booleanValue() && !((AntiSurround)this.module).normal.getValue().booleanValue()) {
            ((AntiSurround)this.module).reset();
            return;
        }
        this.crystalPos = null;
        if (AntiSurround.LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            if (((AntiSurround)this.module).active.get() || ((AntiSurround)this.module).semiActive.get()) {
                AntiSurround antiSurround = (AntiSurround)this.module;
                synchronized (antiSurround) {
                    ((AntiSurround)this.module).reset();
                }
            }
            return;
        }
        AntiSurround antiSurround = (AntiSurround)this.module;
        synchronized (antiSurround) {
            if (((AntiSurround)this.module).active.get()) {
                EntityPlayer target = ((AntiSurround)this.module).target;
                if (target == null || EntityUtil.isDead(target)) {
                    ((AntiSurround)this.module).reset();
                    return;
                }
                IBlockState state = ListenerObby.mc.world.getBlockState(PositionUtil.getPosition(target));
                if (!state.func_185904_a().isReplaceable() && state.getBlock().getExplosionResistance(ListenerObby.mc.player) > 100.0f) {
                    ((AntiSurround)this.module).reset();
                    return;
                }
                if (((AntiSurround)this.module).pos == null) {
                    ((AntiSurround)this.module).reset();
                    return;
                }
                BlockStateHelper helper = new BlockStateHelper();
                helper.addAir(((AntiSurround)this.module).pos);
                float damage = DamageUtil.calculate(((AntiSurround)this.module).pos, target, helper);
                if (damage < ((AntiSurround)this.module).minDmg.getValue().floatValue()) {
                    ((AntiSurround)this.module).reset();
                    return;
                }
            } else if (((AntiSurround)this.module).semiActive.get() && System.nanoTime() - ((AntiSurround)this.module).semiActiveTime > TimeUnit.MILLISECONDS.toNanos(15L)) {
                ((AntiSurround)this.module).semiActive.set(false);
            }
        }
        if (!((AntiSurround)this.module).active.get() && event.getStage() == Stage.PRE && ((AntiSurround)this.module).persistent.getValue().booleanValue() && !((AntiSurround)this.module).holdingCheck()) {
            MineSlots mine = HelperLiquids.getSlots(((AntiSurround)this.module).onGround.getValue());
            if (mine.getBlockSlot() == -1 || mine.getToolSlot() == -1 || mine.getDamage() < ((AntiSurround)this.module).minMine.getValue().floatValue() && !(((AntiSurround)this.module).isAnvil = ((AntiSurround)this.module).anvilCheck(mine))) {
                return;
            }
            int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
            if (crystalSlot == -1) {
                return;
            }
            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
            for (EntityPlayer player : ListenerObby.mc.world.playerEntities) {
                if (player == null || EntityUtil.isDead(player) || player.equals(ListenerObby.mc.player) || player.equals(RotationUtil.getRotationPlayer()) || Managers.FRIENDS.contains(player) || player.getDistanceSq(RotationUtil.getRotationPlayer()) > MathUtil.square(((AntiSurround)this.module).range.getValue() + 2.0)) continue;
                BlockPos playerPos = PositionUtil.getPosition(player);
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    int slot;
                    double damage;
                    IBlockState state;
                    Entity blocking;
                    BlockPos down;
                    BlockPos pos = playerPos.offset(facing);
                    if (BlockUtil.getDistanceSq(pos) > MathUtil.square(((AntiSurround)this.module).range.getValue()) || BlockUtil.getDistanceSq(down = pos.offset(facing).down()) > MathUtil.square(((AntiSurround)this.module).range.getValue()) || (blocking = ((AntiSurround)this.module).getBlockingEntity(pos, ListenerObby.mc.world.loadedEntityList)) != null && !(blocking instanceof EntityEnderCrystal) || (state = ListenerObby.mc.world.getBlockState(pos)).func_185904_a().isReplaceable() || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.ENDER_CHEST || (damage = (double)MineUtil.getDamage(state, ListenerObby.mc.player.inventory.getStackInSlot(slot = MineUtil.findBestTool(playerPos, state)), playerPos, RotationUtil.getRotationPlayer().onGround)) < (double)((AntiSurround)this.module).minMine.getValue().floatValue() || !BlockUtil.canPlaceCrystalReplaceable(down, true, ((AntiSurround)this.module).newVer.getValue(), ListenerObby.mc.world.loadedEntityList, ((AntiSurround)this.module).newVerEntities.getValue(), 0L)) continue;
                    IBlockState dState = ListenerObby.mc.world.getBlockState(down);
                    if ((!((AntiSurround)this.module).obby.getValue().booleanValue() || obbySlot == -1) && dState.getBlock() != Blocks.OBSIDIAN && dState.getBlock() != Blocks.BEDROCK) continue;
                    BlockPos on = null;
                    EnumFacing onFacing = null;
                    for (EnumFacing off : EnumFacing.values()) {
                        on = pos.offset(off);
                        if (!(BlockUtil.getDistanceSq(on) <= MathUtil.square(((AntiSurround)this.module).range.getValue())) || ListenerObby.mc.world.getBlockState(on).func_185904_a().isReplaceable()) continue;
                        onFacing = off.getOpposite();
                        break;
                    }
                    if (onFacing == null) continue;
                    AntiSurround antiSurround2 = (AntiSurround)this.module;
                    synchronized (antiSurround2) {
                        if (!((AntiSurround)this.module).isActive()) {
                            ((AntiSurround)this.module).semiPos = null;
                        }
                        if (((AntiSurround)this.module).placeSync(pos, down, on, onFacing, obbySlot, mine, crystalSlot, blocking, player, false)) {
                            ((AntiSurround)this.module).toolSlot = slot;
                            ((AntiSurround)this.module).mine = true;
                            if (((AntiSurround)this.module).rotations != null && ((AntiSurround)this.module).rotate.getValue() != Rotate.None) {
                                this.setRotations(((AntiSurround)this.module).rotations, event);
                            } else {
                                ((AntiSurround)this.module).execute();
                            }
                        }
                    }
                    return;
                }
            }
        }
        antiSurround = (AntiSurround)this.module;
        synchronized (antiSurround) {
            if (!((AntiSurround)this.module).active.get()) {
                if (((AntiSurround)this.module).semiActive.get() && System.nanoTime() - ((AntiSurround)this.module).semiActiveTime > TimeUnit.MILLISECONDS.toNanos(15L)) {
                    ((AntiSurround)this.module).semiActive.set(false);
                }
                return;
            }
            if (((AntiSurround)this.module).holdingCheck()) {
                ((AntiSurround)this.module).reset();
                return;
            }
            super.invoke(event);
        }
    }

    @Override
    protected boolean updatePlaced() {
        super.updatePlaced();
        if (((AntiSurround)this.module).pos == null || ((AntiSurround)this.module).crystalPos == null) {
            ((AntiSurround)this.module).reset();
        }
        return !((AntiSurround)this.module).active.get();
    }

    @Override
    protected boolean hasTimerNotPassed() {
        boolean result = super.hasTimerNotPassed();
        if (((AntiSurround)this.module).isAnvil && ((AntiSurround)this.module).pos != null) {
            if (!((AntiSurround)this.module).hasMined) {
                this.mine(((AntiSurround)this.module).pos);
                return false;
            }
            if (++((AntiSurround)this.module).ticks < 4) {
                return false;
            }
            if (!result) {
                return false;
            }
        }
        return result;
    }

    private void mine(BlockPos pos) {
        EnumFacing facing = RayTraceUtil.getFacing(RotationUtil.getRotationPlayer(), pos, true);
        PacketUtil.startDigging(pos, facing);
        if (((AntiSurround)this.module).digSwing.getValue().booleanValue()) {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
        ((AntiSurround)this.module).hasMined = true;
        ((AntiSurround)this.module).ticks = 0;
    }

    @Override
    protected int getSlot() {
        ((AntiSurround)this.module).obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
        MineSlots slots = HelperLiquids.getSlots(((AntiSurround)this.module).onGround.getValue());
        if (slots.getDamage() < ((AntiSurround)this.module).minMine.getValue().floatValue() && !(((AntiSurround)this.module).isAnvil = ((AntiSurround)this.module).anvilCheck(slots)) || slots.getToolSlot() == -1 || slots.getBlockSlot() == -1) {
            ((AntiSurround)this.module).reset();
            return -1;
        }
        ((AntiSurround)this.module).crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
        if (((AntiSurround)this.module).crystalSlot == -1) {
            ((AntiSurround)this.module).reset();
            return -1;
        }
        ((AntiSurround)this.module).toolSlot = slots.getToolSlot();
        return slots.getBlockSlot();
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        BlockPos pos = ((AntiSurround)this.module).pos;
        BlockPos crystalPos = ((AntiSurround)this.module).crystalPos;
        if (pos == null || crystalPos == null) {
            result.setValid(false);
            return result;
        }
        if (ListenerObby.mc.world.getBlockState(pos).func_185904_a().isReplaceable()) {
            AntiSurround.HELPER.addBlockState(pos, Blocks.AIR.getDefaultState());
            result.getTargets().add(pos);
        } else if (this.entityCheck(pos)) {
            AntiSurround.HELPER.addBlockState(pos, Blocks.AIR.getDefaultState());
            ((AntiSurround)this.module).mine = true;
            result.getTargets().add(pos);
        } else {
            this.placeObby(crystalPos, result);
        }
        return result;
    }

    @Override
    protected void disableModule() {
        ((AntiSurround)this.module).reset();
    }

    @Override
    protected boolean rotateCheck() {
        if (this.crystalPos != null && (!((AntiSurround)this.module).isAnvil || ((AntiSurround)this.module).ticks > 3 && ((AntiSurround)this.module).hasMined)) {
            BlockStateHelper helper = new BlockStateHelper();
            helper.addBlockState(this.crystalPos, Blocks.OBSIDIAN.getDefaultState());
            RayTraceResult ray = null;
            if (((AntiSurround)this.module).rotations != null) {
                ray = RotationUtil.rayTraceWithYP(this.crystalPos, helper, ((AntiSurround)this.module).rotations[0], ((AntiSurround)this.module).rotations[1], (b, p) -> p.equals(this.crystalPos));
            }
            if (ray == null) {
                double x = RotationUtil.getRotationPlayer().posX;
                double y = RotationUtil.getRotationPlayer().posY;
                double z = RotationUtil.getRotationPlayer().posZ;
                ((AntiSurround)this.module).rotations = RotationUtil.getRotations((float)this.crystalPos.getX() + 0.5f, this.crystalPos.getY() + 1, (float)this.crystalPos.getZ() + 0.5f, x, y, z, ListenerObby.mc.player.getEyeHeight());
                ray = RotationUtil.rayTraceWithYP(this.crystalPos, helper, ((AntiSurround)this.module).rotations[0], ((AntiSurround)this.module).rotations[1], (b, p) -> p.equals(this.crystalPos));
                if (ray == null) {
                    ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, this.crystalPos);
                }
            }
            int crystalSlot = ((AntiSurround)this.module).crystalSlot;
            RayTraceResult finalResult = ray;
            float[] f = RayTraceUtil.hitVecToPlaceVec(this.crystalPos, ray.hitVec);
            EnumHand h = InventoryUtil.getHand(crystalSlot);
            BlockPos finalPos = this.crystalPos;
            ((AntiSurround)this.module).post.add(() -> {
                InventoryUtil.switchTo(crystalSlot);
                ListenerObby.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(finalPos, finalResult.sideHit, h, f[0], f[1], f[2]));
            });
        }
        return super.rotateCheck();
    }

    private void placeObby(BlockPos crystalPos, TargetResult result) {
        if (((AntiSurround)this.module).crystalSlot == -1) {
            ((AntiSurround)this.module).reset();
            result.setValid(false);
            return;
        }
        List<Entity> entities = ListenerObby.mc.world.loadedEntityList;
        if (!((AntiSurround)this.module).attackTimer.passed(((AntiSurround)this.module).itemDeathTime.getValue().intValue())) {
            entities = entities.stream().filter(e -> !(e instanceof EntityItem)).collect(Collectors.toList());
        }
        if (!BlockUtil.canPlaceCrystalReplaceable(crystalPos, true, ((AntiSurround)this.module).newVer.getValue(), entities, ((AntiSurround)this.module).newVerEntities.getValue(), 0L)) {
            ((AntiSurround)this.module).reset();
            result.setValid(false);
            return;
        }
        IBlockState state = ListenerObby.mc.world.getBlockState(crystalPos);
        if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) {
            if (!state.func_185904_a().isReplaceable() || !((AntiSurround)this.module).obby.getValue().booleanValue() || ((AntiSurround)this.module).obbySlot == -1) {
                ((AntiSurround)this.module).reset();
                result.setValid(false);
                return;
            }
            result.getTargets().add(crystalPos);
            ((AntiSurround)this.module).slot = ((AntiSurround)this.module).obbySlot;
        }
        this.crystalPos = crystalPos;
    }

    private boolean entityCheck(BlockPos pos) {
        BlockPos boost1 = pos.up();
        for (Entity entity : ListenerObby.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(boost1))) {
            if (entity == null || EntityUtil.isDead(entity)) continue;
            return true;
        }
        return false;
    }
}
