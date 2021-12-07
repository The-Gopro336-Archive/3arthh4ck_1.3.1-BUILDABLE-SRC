/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.combat.bomber;

import java.util.List;
import java.util.stream.Collectors;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.bomber.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.bomber.enums.CrystalBomberMode;
import me.earth.earthhack.impl.modules.combat.bomber.enums.CrystalBomberStage;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class CrystalBomber
extends Module {
    protected final Setting<CrystalBomberMode> mode = this.register(new EnumSetting<CrystalBomberMode>("Mode", CrystalBomberMode.Normal));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Float> toggleAt = this.register(new NumberSetting<Float>("ToggleAt", Float.valueOf(8.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Float> enemyRange = this.register(new NumberSetting<Float>("EnemyRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(16.0f)));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 0, 0, 500));
    protected final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> reCheckCrystal = this.register(new BooleanSetting("ReCheckCrystal", false));
    protected final Setting<Boolean> airCheck = this.register(new BooleanSetting("AirCheck", false));
    protected final Setting<Boolean> smartSneak = this.register(new BooleanSetting("Smart-Sneak", true));
    protected final Setting<Boolean> bypass = this.register(new BooleanSetting("Bypass", false));
    private static final ModuleCache<Speedmine> SPEEDMINE = Caches.getModule(Speedmine.class);
    private static EntityPlayer target;
    private Vec3d lastTargetPos;
    private BlockPos targetPos;
    private int lastSlot;
    private boolean hasHit;
    public boolean rotating = false;
    private float yaw = 0.0f;
    private float targetYaw = 0.0f;
    private float pitch = 0.0f;
    private int rotationPacketsSpoofed = 0;
    private boolean offhand;
    private final StopWatch timer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();
    private final StopWatch cooldownTimer = new StopWatch();
    private CrystalBomberStage stage = CrystalBomberStage.FirstHit;
    private boolean firstHit = false;

    public CrystalBomber() {
        super("CrystalBomber", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    protected void onEnable() {
        this.targetPos = null;
        this.lastTargetPos = null;
        target = null;
        this.stage = CrystalBomberStage.FirstHit;
        this.timer.reset();
        this.delayTimer.reset();
        this.cooldownTimer.reset();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected void doCrystalBomber(MotionUpdateEvent event) {
        block53: {
            if (event.getStage() != Stage.PRE) break block53;
            this.updateTarget();
            if (CrystalBomber.target == null) return;
            if (this.targetPos != null) {
                this.lastTargetPos = new Vec3d((Vec3i)this.targetPos);
            }
            this.targetPos = PositionUtil.getPosition((Entity)CrystalBomber.target).up().up();
            if (this.lastTargetPos != null && !this.lastTargetPos.equals((Object)new Vec3d((Vec3i)this.targetPos))) {
                this.stage = CrystalBomberStage.FirstHit;
                this.firstHit = true;
            }
            if (this.delayTimer.passed(this.delay.getValue().intValue()) == false) return;
            if (this.reCheckCrystal.getValue().booleanValue()) {
                this.recheckCrystal();
            }
            switch (1.$SwitchMap$me$earth$earthhack$impl$modules$combat$bomber$enums$CrystalBomberStage[this.stage.ordinal()]) {
                case 1: {
                    if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() != Blocks.AIR && MineUtil.canBreak(this.targetPos)) {
                        this.rotateToPos(this.targetPos, event);
                        return;
                    }
                }
                case 2: {
                    if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() == Blocks.OBSIDIAN) {
                        if (BlockUtil.canPlaceCrystal(this.targetPos, false, false) == false) return;
                        this.rotateToPos(this.targetPos, event);
                        return;
                    }
                    this.stage = CrystalBomberStage.PlaceObsidian;
                    this.delayTimer.reset();
                    return;
                }
                case 3: {
                    if (this.firstHit) {
                        if (this.isValidForMining() == false) return;
                        this.rotateToPos(this.targetPos, event);
                        return;
                    }
                    this.rotateToPos(this.targetPos, event);
                    return;
                }
                case 4: {
                    crystals = CrystalBomber.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.targetPos.up()));
                    if (crystals.isEmpty()) ** GOTO lbl41
                    if ((CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() == Blocks.AIR || !this.airCheck.getValue().booleanValue()) && this.cooldownTimer.passed(this.cooldown.getValue().intValue()) && (crystal = (EntityEnderCrystal)crystals.get(0)) != null) {
                        this.rotateTo((Entity)crystal, event);
                        return;
                    }
                    ** GOTO lbl46
lbl41:
                    // 1 sources

                    if (this.reCheckCrystal.getValue().booleanValue()) {
                        this.stage = CrystalBomberStage.Crystal;
                        this.delayTimer.reset();
                        return;
                    }
                }
lbl46:
                // 4 sources

                case 5: {
                    obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
                    v0 = offhand = CrystalBomber.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock != false && ((ItemBlock)CrystalBomber.mc.player.getHeldItemOffhand().getItem()).getBlock() == Blocks.OBSIDIAN;
                    if (obbySlot == -1) {
                        if (offhand == false) return;
                    }
                    if (BlockUtil.isReplaceable(this.targetPos)) {
                        if (!(CrystalBomber.mc.player.getDistanceSq(this.targetPos) <= (double)MathUtil.square(this.range.getValue().floatValue()))) return;
                        this.rotateToPos(this.targetPos, event);
                        return;
                    }
                    if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() != Blocks.OBSIDIAN) return;
                    if (this.mode.getValue() == CrystalBomberMode.Instant) {
                        this.stage = CrystalBomberStage.Crystal;
                        return;
                    }
                    this.stage = CrystalBomberStage.FirstHit;
                    return;
                }
            }
            return;
        }
        if (event.getStage() != Stage.POST) return;
        this.updateTarget();
        if (CrystalBomber.target == null) return;
        if (this.delayTimer.passed(this.delay.getValue().intValue()) == false) return;
        switch (1.$SwitchMap$me$earth$earthhack$impl$modules$combat$bomber$enums$CrystalBomberStage[this.stage.ordinal()]) {
            case 1: {
                if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() != Blocks.AIR) {
                    if (((Speedmine)CrystalBomber.SPEEDMINE.get()).getPos() == null || !new Vec3d((Vec3i)((Speedmine)CrystalBomber.SPEEDMINE.get()).getPos()).equals((Object)new Vec3d((Vec3i)this.targetPos))) {
                        CrystalBomber.mc.playerController.onPlayerDamageBlock(this.targetPos, CrystalBomber.mc.player.getHorizontalFacing().getOpposite());
                    } else if (new Vec3d((Vec3i)((Speedmine)CrystalBomber.SPEEDMINE.get()).getPos()).equals((Object)new Vec3d((Vec3i)this.targetPos)) && (((Speedmine)CrystalBomber.SPEEDMINE.get()).getMode() == MineMode.Instant || ((Speedmine)CrystalBomber.SPEEDMINE.get()).getMode() == MineMode.Civ)) {
                        this.stage = CrystalBomberStage.Crystal;
                        this.delayTimer.reset();
                        this.timer.reset();
                        this.firstHit = false;
                        return;
                    }
                    this.stage = CrystalBomberStage.Crystal;
                    this.delayTimer.reset();
                    this.timer.reset();
                    this.firstHit = true;
                    return;
                }
            }
            case 2: {
                crystalSlot = this.getCrsytalSlot();
                v1 = this.offhand = CrystalBomber.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
                if (!this.offhand) {
                    this.lastSlot = CrystalBomber.mc.player.inventory.currentItem;
                    if (crystalSlot != -1) {
                        if (this.bypass.getValue().booleanValue()) {
                            InventoryUtil.switchToBypass(crystalSlot);
                        } else {
                            InventoryUtil.switchTo(crystalSlot);
                        }
                    }
                }
                if ((this.offhand || CrystalBomber.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) && CrystalBomber.mc.player.getDistanceSq(this.targetPos) <= (double)MathUtil.square(this.range.getValue().floatValue())) {
                    CrystalBomber.placeCrystalOnBlock(this.targetPos, this.offhand != false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, false);
                }
                this.stage = CrystalBomberStage.Pickaxe;
                this.delayTimer.reset();
                return;
            }
            case 3: {
                if (!this.firstHit) ** GOTO lbl128
                if (this.isValidForMining()) {
                    pickSlot = this.getPickSlot();
                    lastSlot = CrystalBomber.mc.player.inventory.currentItem;
                    if (pickSlot != -1) {
                        if (this.bypass.getValue().booleanValue()) {
                            InventoryUtil.switchToBypass(pickSlot);
                        } else {
                            InventoryUtil.switchTo(pickSlot);
                        }
                        ((Speedmine)CrystalBomber.SPEEDMINE.get()).forceSend();
                        this.stage = CrystalBomberStage.Explode;
                        if (this.bypass.getValue().booleanValue()) {
                            InventoryUtil.switchToBypass(pickSlot);
                        } else {
                            InventoryUtil.switchTo(lastSlot);
                        }
                        this.delayTimer.reset();
                        this.cooldownTimer.reset();
                        this.firstHit = false;
                        return;
                    }
                }
                ** GOTO lbl146
lbl128:
                // 1 sources

                pickSlot = this.getPickSlot();
                lastSlot = CrystalBomber.mc.player.inventory.currentItem;
                if (pickSlot != -1) {
                    if (this.bypass.getValue().booleanValue()) {
                        InventoryUtil.switchToBypass(pickSlot);
                    } else {
                        InventoryUtil.switchTo(pickSlot);
                    }
                    ((Speedmine)CrystalBomber.SPEEDMINE.get()).forceSend();
                    this.stage = CrystalBomberStage.Explode;
                    this.delayTimer.reset();
                    this.cooldownTimer.reset();
                    if (this.bypass.getValue().booleanValue()) {
                        InventoryUtil.switchToBypass(lastSlot);
                        return;
                    }
                    InventoryUtil.switchTo(lastSlot);
                    return;
                }
            }
lbl146:
            // 4 sources

            case 4: {
                crystals = CrystalBomber.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.targetPos.up()));
                if (!this.cooldownTimer.passed(this.cooldown.getValue().intValue())) {
                    this.stage = CrystalBomberStage.Explode;
                    return;
                }
                if (crystals.isEmpty() || CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() != Blocks.AIR && this.airCheck.getValue().booleanValue()) ** GOTO lbl168
                crystal = (EntityEnderCrystal)crystals.get(0);
                if (crystal != null) {
                    this.rotating = false;
                    if (CrystalBomber.mc.player.getDistanceSq((Entity)crystal) <= (double)MathUtil.square(this.range.getValue().floatValue())) {
                        CrystalBomber.attackEntity((Entity)crystal, true, true);
                        this.stage = CrystalBomberStage.PlaceObsidian;
                        this.delayTimer.reset();
                        return;
                    }
                } else if (this.reCheckCrystal.getValue().booleanValue()) {
                    this.stage = CrystalBomberStage.Crystal;
                    this.delayTimer.reset();
                    return;
                }
                ** GOTO lbl173
lbl168:
                // 1 sources

                if (this.reCheckCrystal.getValue().booleanValue()) {
                    this.stage = CrystalBomberStage.Crystal;
                    this.delayTimer.reset();
                    return;
                }
            }
lbl173:
            // 5 sources

            case 5: {
                obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
                v2 = offhand = CrystalBomber.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock != false && ((ItemBlock)CrystalBomber.mc.player.getHeldItemOffhand().getItem()).getBlock() == Blocks.OBSIDIAN;
                if (obbySlot == -1) {
                    if (offhand == false) return;
                }
                if (BlockUtil.isReplaceable(this.targetPos) || BlockUtil.isAir(this.targetPos)) {
                    if (CrystalBomber.mc.player.getDistanceSq(this.targetPos) <= (double)MathUtil.square(this.range.getValue().floatValue()) && (facing = BlockUtil.getFacing(this.targetPos)) != null) {
                        rotations = RotationUtil.getRotations(this.targetPos.offset(facing), facing.getOpposite());
                        this.placeBlock(this.targetPos.offset(facing), facing.getOpposite(), rotations, obbySlot);
                    }
                    this.stage = this.mode.getValue() == CrystalBomberMode.Instant ? CrystalBomberStage.Crystal : CrystalBomberStage.FirstHit;
                    this.delayTimer.reset();
                    return;
                }
                if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() != Blocks.OBSIDIAN) return;
                this.stage = this.mode.getValue() == CrystalBomberMode.Instant ? CrystalBomberStage.Crystal : CrystalBomberStage.FirstHit;
                this.delayTimer.reset();
                return;
            }
        }
    }

    private void updateTarget() {
        List players = CrystalBomber.mc.world.playerEntities.stream().filter(entity -> CrystalBomber.mc.player.getDistanceSq((Entity)entity) <= (double)MathUtil.square(this.enemyRange.getValue().floatValue())).filter(entity -> !Managers.FRIENDS.contains((EntityPlayer)entity)).collect(Collectors.toList());
        EntityPlayer currentPlayer = null;
        for (EntityPlayer player : players) {
            if (player == CrystalBomber.mc.player) continue;
            if (currentPlayer == null) {
                currentPlayer = player;
            }
            if (!(CrystalBomber.mc.player.getDistanceSq((Entity)player) < CrystalBomber.mc.player.getDistanceSq((Entity)currentPlayer))) continue;
            currentPlayer = player;
        }
        target = currentPlayer;
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (CrystalBomber.mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    private int getCrsytalSlot() {
        for (int i = 0; i < 9; ++i) {
            if (CrystalBomber.mc.player.inventory.getStackInSlot(i).getItem() != Items.END_CRYSTAL) continue;
            return i;
        }
        return -1;
    }

    private void rotateTo(Entity entity, MotionUpdateEvent event) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = RotationUtil.getRotations(entity);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void rotateToPos(BlockPos pos, MotionUpdateEvent event) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = RotationUtil.getRotationsToTopMiddle(pos);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void recheckCrystal() {
        if (CrystalBomber.mc.world.getBlockState(this.targetPos).getBlock() == Blocks.OBSIDIAN && CrystalBomber.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.targetPos.up())).isEmpty() && this.stage != CrystalBomberStage.FirstHit) {
            this.stage = CrystalBomberStage.Crystal;
        }
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
        RayTraceResult result = CrystalBomber.mc.world.rayTraceBlocks(new Vec3d(CrystalBomber.mc.player.posX, CrystalBomber.mc.player.posY + (double)CrystalBomber.mc.player.getEyeHeight(), CrystalBomber.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (swing) {
            CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
        } else {
            CrystalBomber.mc.playerController.attackEntity((EntityPlayer)CrystalBomber.mc.player, entity);
        }
        if (swingArm) {
            CrystalBomber.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public boolean isValidForMining() {
        int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE, new Item[0]);
        if (pickSlot == -1) {
            return false;
        }
        return ((Speedmine)CrystalBomber.SPEEDMINE.get()).damages[pickSlot] >= ((Speedmine)CrystalBomber.SPEEDMINE.get()).limit.getValue().floatValue();
    }

    protected void placeBlock(BlockPos on, EnumFacing facing, float[] rotations, int slot) {
        if (rotations != null) {
            boolean sneaking;
            RayTraceResult result = RayTraceUtil.getRayTraceResult(rotations[0], rotations[1]);
            float[] f = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);
            int lastSlot = CrystalBomber.mc.player.inventory.currentItem;
            boolean bl = sneaking = this.smartSneak.getValue() != false && !SpecialBlocks.shouldSneak(on, true);
            if (this.bypass.getValue().booleanValue()) {
                InventoryUtil.switchToBypass(slot);
            } else {
                InventoryUtil.switchTo(slot);
            }
            if (!sneaking) {
                CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)CrystalBomber.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(on, facing, InventoryUtil.getHand(slot), f[0], f[1], f[2]));
            CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketAnimation(InventoryUtil.getHand(slot)));
            if (!sneaking) {
                CrystalBomber.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)CrystalBomber.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            if (CrystalBomber.mc.player.inventory.getStackInSlot(InventoryUtil.hotbarToInventory(lastSlot)).getItem() != Items.DIAMOND_PICKAXE) {
                if (this.bypass.getValue().booleanValue()) {
                    InventoryUtil.switchToBypass(lastSlot);
                } else {
                    InventoryUtil.switchTo(lastSlot);
                }
            }
        }
    }
}

