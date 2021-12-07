/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion
extends ModuleListener<PistonAura, MotionUpdateEvent> {
    private static final ModuleCache<NoGlitchBlocks> NO_GLITCH_BLOCKS = Caches.getModule(NoGlitchBlocks.class);

    public ListenerMotion(PistonAura module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            ((PistonAura)this.module).clicked.clear();
            ((PistonAura)this.module).blocksPlaced = 0;
            ((PistonAura)this.module).rotations = null;
            ((PistonAura)this.module).pistonSlot = InventoryUtil.findHotbarBlock((Block)Blocks.PISTON, new Block[]{Blocks.STICKY_PISTON});
            if (((PistonAura)this.module).pistonSlot == -1 && this.shouldDisable(PistonStage.PISTON)) {
                ((PistonAura)this.module).disableWithMessage("<" + ((PistonAura)this.module).getDisplayName() + "> " + "\u00a7c" + "No Pistons found!");
                return;
            }
            ((PistonAura)this.module).redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_TORCH);
            if (((PistonAura)this.module).redstoneSlot == -1 && this.shouldDisable(PistonStage.REDSTONE)) {
                ((PistonAura)this.module).disableWithMessage("<" + ((PistonAura)this.module).getDisplayName() + "> " + "\u00a7c" + "No Redstone found!");
                return;
            }
            ((PistonAura)this.module).crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
            if (((PistonAura)this.module).crystalSlot == -1 && this.shouldDisable(PistonStage.CRYSTAL)) {
                ((PistonAura)this.module).disableWithMessage("<" + ((PistonAura)this.module).getDisplayName() + "> " + "\u00a7c" + "No Crystals found!");
                return;
            }
            if (((PistonAura)this.module).reset && ((PistonAura)this.module).nextTimer.passed(((PistonAura)this.module).next.getValue().intValue()) && ((PistonAura)this.module).current != null) {
                ((PistonAura)this.module).current.setValid(false);
            }
            if (((PistonAura)this.module).current == null || !((PistonAura)this.module).current.isValid()) {
                ((PistonAura)this.module).current = ((PistonAura)this.module).findTarget();
                if (((PistonAura)this.module).current == null || !((PistonAura)this.module).current.isValid()) {
                    return;
                }
            }
            ((PistonAura)this.module).stage = ((PistonAura)this.module).current.getOrder()[((PistonAura)this.module).index];
            while (((PistonAura)this.module).index < 3 && ((PistonAura)this.module).stage == null) {
                ++((PistonAura)this.module).index;
                ((PistonAura)this.module).stage = ((PistonAura)this.module).current.getOrder()[((PistonAura)this.module).index];
            }
            while (((PistonAura)this.module).blocksPlaced < (Integer)((PistonAura)this.module).blocks.getValue() && this.runPre() && (((PistonAura)this.module).rotations == null || ((PistonAura)this.module).rotate.getValue() != Rotate.Normal)) {
            }
            if (((PistonAura)this.module).blocksPlaced > 0) {
                ((PistonAura)this.module).timer.reset(((Integer)((PistonAura)this.module).delay.getValue()).intValue());
            }
            if (((PistonAura)this.module).rotations != null) {
                event.setYaw(((PistonAura)this.module).rotations[0]);
                event.setPitch(((PistonAura)this.module).rotations[1]);
            }
        } else {
            if (((PistonAura)this.module).current == null || !((PistonAura)this.module).current.isValid()) {
                return;
            }
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                boolean sneak;
                ((PistonAura)this.module).slot = ListenerMotion.mc.player.inventory.currentItem;
                boolean bl = sneak = ((PistonAura)this.module).blocksPlaced == 0 || ((PistonAura)this.module).actions.isEmpty() || (Boolean)((PistonAura)this.module).smartSneak.getValue() != false && !Managers.ACTION.isSneaking() && !((PistonAura)this.module).clicked.stream().anyMatch(b -> SpecialBlocks.shouldSneak(b, false));
                if (!sneak) {
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ListenerMotion.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                CollectionUtil.emptyQueue(((PistonAura)this.module).actions);
                if (!sneak) {
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ListenerMotion.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (ListenerMotion.mc.player.inventory.currentItem != ((PistonAura)this.module).slot) {
                    InventoryUtil.switchTo(((PistonAura)this.module).slot);
                }
            });
        }
    }

    private boolean runPre() {
        ((PistonAura)this.module).stage = ((PistonAura)this.module).current.getOrder()[((PistonAura)this.module).index];
        BlockPos pos = ((PistonAura)this.module).stage.getPos(((PistonAura)this.module).current);
        if (((PistonAura)this.module).stage == PistonStage.BREAK) {
            if (!((PistonAura)this.module).explode.getValue().booleanValue()) {
                return false;
            }
            for (EntityEnderCrystal crystal : ListenerMotion.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))) {
                if (!crystal.getPosition().equals((Object)pos.up()) && !crystal.getPosition().equals((Object)((PistonAura)this.module).current.getStartPos().up())) continue;
                float[] crystalRots = RotationUtil.getRotations((Entity)crystal);
                CPacketPlayer.Rotation rotation = null;
                if (((PistonAura)this.module).rotate.getValue() == Rotate.Packet && ((PistonAura)this.module).rotations != null) {
                    rotation = new CPacketPlayer.Rotation(crystalRots[0], crystalRots[1], ListenerMotion.mc.player.onGround);
                } else if (((PistonAura)this.module).rotate.getValue() != Rotate.None) {
                    ((PistonAura)this.module).rotations = crystalRots;
                }
                CPacketPlayer.Rotation finalRotation = rotation;
                ((PistonAura)this.module).actions.add(() -> {
                    if (((PistonAura)this.module).breakTimer.passed(((PistonAura)this.module).breakDelay.getValue().intValue()) && Managers.SWITCH.getLastSwitch() >= (long)((PistonAura)this.module).coolDown.getValue().intValue()) {
                        if (finalRotation != null) {
                            ListenerMotion.mc.player.connection.sendPacket((Packet)finalRotation);
                        }
                        ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)crystal));
                        ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                        ((PistonAura)this.module).breakTimer.reset();
                        ((PistonAura)this.module).nextTimer.reset();
                        ((PistonAura)this.module).reset = true;
                    }
                });
                return false;
            }
            return false;
        }
        if (!((PistonAura)this.module).timer.passed(((Integer)((PistonAura)this.module).delay.getValue()).intValue())) {
            return false;
        }
        if (((PistonAura)this.module).stage == PistonStage.CRYSTAL) {
            for (Entity entity : ListenerMotion.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), ((PistonAura)this.module).current.getStartPos()))) {
                if (entity == null || EntityUtil.isDead(entity)) continue;
                if (entity instanceof EntityEnderCrystal && entity.getPosition().equals((Object)pos.up())) {
                    ++((PistonAura)this.module).index;
                    ((PistonAura)this.module).stage = ((PistonAura)this.module).current.getOrder()[((PistonAura)this.module).index];
                    pos = ((PistonAura)this.module).stage.getPos(((PistonAura)this.module).current);
                    break;
                }
                ((PistonAura)this.module).current.setValid(false);
                return false;
            }
        }
        if (pos != null) {
            EnumHand hand;
            float[] rotations;
            EnumFacing facing = BlockUtil.getFacing(pos);
            if (facing == null && ((PistonAura)this.module).stage != PistonStage.CRYSTAL && (!((Boolean)((PistonAura)this.module).packet.getValue()).booleanValue() || ((PistonAura)this.module).packetTimer.passed(((PistonAura)this.module).confirmation.getValue().intValue())) || ((PistonAura)this.module).stage != PistonStage.CRYSTAL && ((PistonAura)this.module).checkEntities(pos)) {
                ((PistonAura)this.module).current.setValid(false);
                return false;
            }
            if (facing == null && ((PistonAura)this.module).stage != PistonStage.CRYSTAL) {
                return false;
            }
            int slot = ((PistonAura)this.module).getSlot();
            if (slot == -1) {
                ((PistonAura)this.module).disableWithMessage("<" + ((PistonAura)this.module).getDisplayName() + "> " + "\u00a7c" + "Items missing!");
                return false;
            }
            ((PistonAura)this.module).actions.add(() -> InventoryUtil.switchTo(slot));
            if (((PistonAura)this.module).stage == PistonStage.CRYSTAL) {
                RayTraceResult result;
                if (((PistonAura)this.module).rotate.getValue() != Rotate.None) {
                    rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
                    result = RayTraceUtil.getRayTraceResult(rotations[0], rotations[1], ((PistonAura)this.module).placeRange.getValue().floatValue());
                } else {
                    result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
                }
                facing = result.sideHit.getOpposite();
                rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
            } else {
                assert (facing != null);
                rotations = RotationUtil.getRotations(pos.offset(facing), facing.getOpposite());
            }
            EnumHand enumHand = hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            if (((PistonAura)this.module).stage == PistonStage.PISTON && ((PistonAura)this.module).multiDirectional.getValue().booleanValue()) {
                EnumFacing toFace = ((PistonAura)this.module).current.getFacing().getOpposite();
                EnumFacing piston = ((PistonAura)this.module).getFacing(pos, rotations);
                if (piston == EnumFacing.UP || piston == EnumFacing.DOWN) {
                    ((PistonAura)this.module).current.setValid(false);
                    return false;
                }
                for (int index = 0; piston != toFace && index < 36; ++index) {
                    rotations[0] = (rotations[0] + 10.0f) % 360.0f;
                    piston = ((PistonAura)this.module).getFacing(pos, rotations);
                }
                if (piston != toFace) {
                    return false;
                }
            }
            switch ((Rotate)((Object)((PistonAura)this.module).rotate.getValue())) {
                case None: {
                    if (((PistonAura)this.module).stage != PistonStage.PISTON) break;
                }
                case Normal: {
                    if (((PistonAura)this.module).rotations == null) {
                        ((PistonAura)this.module).rotations = rotations;
                        break;
                    }
                    return false;
                }
                case Packet: {
                    CPacketPlayer.Rotation rotation = new CPacketPlayer.Rotation(rotations[0], rotations[1], ListenerMotion.mc.player.onGround);
                    ((PistonAura)this.module).actions.add(() -> ListenerMotion.lambda$runPre$4((CPacketPlayer)rotation));
                    break;
                }
            }
            RayTraceResult result = RayTraceUtil.getRayTraceResult(rotations[0], rotations[1]);
            float[] f = RayTraceUtil.hitVecToPlaceVec(pos.offset(facing), result.hitVec);
            BlockPos on = ((PistonAura)this.module).stage == PistonStage.CRYSTAL ? pos : pos.offset(facing);
            ((PistonAura)this.module).clicked.add(ListenerMotion.mc.world.getBlockState(on).getBlock());
            CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(on, facing.getOpposite(), hand, f[0], f[1], f[2]);
            if (!(((PistonAura)this.module).stage == PistonStage.CRYSTAL || ((Boolean)((PistonAura)this.module).packet.getValue()).booleanValue() || NO_GLITCH_BLOCKS.isPresent() && ((NoGlitchBlocks)NO_GLITCH_BLOCKS.get()).noPlace())) {
                ItemStack stack = slot == -2 ? ListenerMotion.mc.player.getHeldItemOffhand() : ListenerMotion.mc.player.inventory.getStackInSlot(slot);
                ((PistonAura)this.module).placeClient(stack, on, InventoryUtil.getHand(slot), facing, f[0], f[1], f[2]);
            }
            ((PistonAura)this.module).actions.add(() -> {
                ListenerMotion.mc.player.connection.sendPacket((Packet)place);
                ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketAnimation(hand));
                if (((Boolean)((PistonAura)this.module).swing.getValue()).booleanValue()) {
                    Swing.Client.swing(hand);
                }
                ((PistonAura)this.module).packetTimer.reset();
            });
            ++((PistonAura)this.module).blocksPlaced;
            ((PistonAura)this.module).index = ((PistonAura)this.module).index == 4 ? 4 : ((PistonAura)this.module).index + 1;
            return ((PistonAura)this.module).rotate.getValue() != Rotate.Normal;
        }
        return false;
    }

    private boolean shouldDisable(PistonStage missing) {
        if (((PistonAura)this.module).current == null || !((PistonAura)this.module).current.isValid()) {
            return true;
        }
        if (((PistonAura)this.module).stage == PistonStage.BREAK) {
            return false;
        }
        for (int i = ((PistonAura)this.module).index; i < 4; ++i) {
            if (((PistonAura)this.module).current.getOrder()[i] != missing) continue;
            return true;
        }
        return false;
    }

    private static /* synthetic */ void lambda$runPre$4(CPacketPlayer rotation) {
        ListenerMotion.mc.player.connection.sendPacket((Packet)rotation);
    }
}

