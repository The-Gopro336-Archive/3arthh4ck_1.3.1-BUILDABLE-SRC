/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.util.helpers.blocks;

import java.util.ArrayList;
import java.util.Collection;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackingModule;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.FastHelping;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class ObbyModule
extends BlockPlacingModule
implements AttackingModule {
    public static final BlockStateHelper HELPER = new BlockStateHelper();
    public final Setting<Boolean> attack = this.register(new BooleanSetting("Attack", false));
    public final Setting<Pop> pop = this.register(new EnumSetting<Pop>("Pop", Pop.None));
    public final Setting<Integer> popTime = this.register(new NumberSetting<Integer>("Pop-Time", 500, 0, 500));
    public final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("Cooldown", 500, 0, 500));
    public final Setting<Boolean> antiWeakness = this.register(new BooleanSetting("AntiWeakness", false));
    public final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 250, 0, 500));
    public final Setting<FastHelping> fastHelpingBlocks = this.register(new EnumSetting<FastHelping>("Fast-Helping", FastHelping.Fast));
    public final Setting<Boolean> attackAny = this.register(new BooleanSetting("Attack-Any", false));
    public final Setting<Double> attackRange = this.register(new NumberSetting<Double>("Attack-Range", 6.0, 0.0, 6.0));
    public final Setting<Double> attackTrace = this.register(new NumberSetting<Double>("Attack-Trace", 3.0, 0.0, 6.0));
    public final StopWatch attackTimer = new StopWatch();
    public CPacketUseEntity attacking = null;

    protected ObbyModule(String name, Category category) {
        super(name, category);
        this.setData(new ObbyData<ObbyModule>(this));
    }

    @Override
    public Pop getPop() {
        return this.pop.getValue();
    }

    @Override
    public double getRange() {
        return this.attackRange.getValue();
    }

    @Override
    public int getPopTime() {
        return this.popTime.getValue();
    }

    @Override
    public double getTrace() {
        return this.attackTrace.getValue();
    }

    @Override
    public String getDisplayInfo() {
        double time = MathUtil.round((double)this.timer.getTime() / 1000.0, 1);
        return (time > 0.5 ? (time > 1.0 ? (time > 1.5 ? "\u00a7a" : "\u00a7e") : "\u00a76") : "\u00a7c") + time;
    }

    @Override
    protected void onEnable() {
        this.attacking = null;
    }

    @Override
    public boolean execute() {
        this.lastSlot = -1;
        if (!this.packets.isEmpty()) {
            if (this.attacking != null) {
                boolean switched = false;
                int slot = -1;
                if (!DamageUtil.canBreakWeakness(true)) {
                    if (this.cooldown.getValue() != 0 || !this.antiWeakness.getValue().booleanValue() || (slot = DamageUtil.findAntiWeakness()) == -1) {
                        this.filterPackets();
                        if (this.packets.isEmpty()) {
                            return false;
                        }
                        return super.execute();
                    }
                    this.lastSlot = ObbyModule.mc.player.inventory.currentItem;
                    switch ((CooldownBypass)((Object)this.cooldownBypass.getValue())) {
                        case None: {
                            InventoryUtil.switchTo(slot);
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(slot));
                            switched = true;
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(slot);
                            switched = true;
                        }
                    }
                }
                ObbyModule.mc.player.connection.sendPacket((Packet)this.attacking);
                Swing.Packet.swing(EnumHand.MAIN_HAND);
                if (switched) {
                    if (this.cooldownBypass.getValue() == CooldownBypass.Pick) {
                        InventoryUtil.bypassSwitch(slot);
                    } else if (this.cooldownBypass.getValue() == CooldownBypass.Slot) {
                        InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(slot));
                    }
                }
                this.attackTimer.reset();
                this.attacking = null;
            }
            return super.execute();
        }
        return false;
    }

    protected void filterPackets() {
        boolean awaitingSwing = false;
        CPacketPlayer.Rotation rotation = null;
        ArrayList<Object> toRemove = new ArrayList<Object>();
        for (Packet p : this.packets) {
            if (p instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock c = (CPacketPlayerTryUseItemOnBlock)p;
                BlockPos pos = c.getPos().offset(c.getDirection());
                for (Entity entity : ObbyModule.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                    if (EntityUtil.isDead(entity) || !entity.preventEntitySpawning || !RotationUtil.getRotationPlayer().equals((Object)ObbyModule.mc.player) && ObbyModule.mc.player.equals((Object)entity)) continue;
                    if (rotation != null) {
                        toRemove.add((Object)rotation);
                    }
                    toRemove.add((Object)p);
                    awaitingSwing = true;
                }
                continue;
            }
            if (p instanceof CPacketPlayer.Rotation) {
                rotation = (CPacketPlayer.Rotation)p;
                continue;
            }
            if (!awaitingSwing || !(p instanceof CPacketAnimation)) continue;
            awaitingSwing = false;
            toRemove.add((Object)p);
        }
        this.packets.removeAll(toRemove);
    }

    @Override
    protected boolean sneak(Collection<Packet<?>> packets) {
        return (Boolean)this.smartSneak.getValue() != false && !Managers.ACTION.isSneaking() && !packets.stream().anyMatch(p -> SpecialBlocks.ACCESS_CHECK.test((Packet<?>)p, HELPER));
    }

    @Override
    public boolean entityCheck(BlockPos pos) {
        CPacketUseEntity attackPacket = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
        for (Entity entity : ObbyModule.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || entity instanceof EntityPlayer && !BlockUtil.isBlocking(pos, (EntityPlayer)entity, (BlockingType)((Object)this.blockingType.getValue()))) continue;
            if (entity instanceof EntityEnderCrystal && this.attackTimer.passed(this.breakDelay.getValue().intValue()) && this.attack.getValue().booleanValue() && Managers.SWITCH.getLastSwitch() >= (long)this.cooldown.getValue().intValue()) {
                float damage = DamageUtil.calculate(entity, (EntityLivingBase)this.getPlayerForRotations());
                if (damage < currentDmg) {
                    currentDmg = damage;
                    if (this.pop.getValue().shouldPop(damage, this.popTime.getValue())) {
                        attackPacket = new CPacketUseEntity(entity);
                        continue;
                    }
                }
                crystals = true;
                continue;
            }
            if (this.blockingType.getValue() == BlockingType.Crystals && entity instanceof EntityEnderCrystal) continue;
            return false;
        }
        if (crystals && attackPacket == null && this.blockingType.getValue() != BlockingType.Crystals) {
            return false;
        }
        if (attackPacket != null) {
            this.attacking = attackPacket;
        }
        return true;
    }

    public boolean placeBlock(BlockPos pos) {
        if (this.smartRay.getValue() != RayTraceMode.Fast) {
            EntityPlayer entity = this.getPlayerForRotations();
            Ray forceRay = null;
            Ray forceHelpingRay = null;
            Ray dumbRay = null;
            Ray dumbHelpingRay = null;
            Ray ray = RayTraceFactory.fullTrace((Entity)entity, HELPER, pos, -1.0);
            if (ray == null || this.shouldHelp(ray.getFacing(), pos) || !ray.getPos().offset(ray.getFacing()).equals((Object)pos) || !ray.isLegit() && (this.smartRay.getValue() == RayTraceMode.Smart || this.smartRay.getValue() == RayTraceMode.Force)) {
                if (ray != null && ray.getPos().offset(ray.getFacing()).equals((Object)pos)) {
                    dumbRay = ray;
                    forceRay = ray;
                }
                for (EnumFacing facing : EnumFacing.values()) {
                    BlockPos helpingPos = pos.offset(facing);
                    IBlockState state = HELPER.getBlockState(helpingPos);
                    if (!state.getMaterial().isReplaceable() || this.quickEntityCheck(helpingPos)) continue;
                    Ray helpingRay = RayTraceFactory.fullTrace((Entity)entity, HELPER, helpingPos, -1.0);
                    if (helpingRay == null || !helpingRay.getPos().offset(helpingRay.getFacing()).equals((Object)helpingPos) || !helpingRay.isLegit() && (this.smartRay.getValue() == RayTraceMode.Smart || this.smartRay.getValue() == RayTraceMode.Force)) {
                        if (dumbRay != null || helpingRay == null || !helpingRay.getPos().offset(helpingRay.getFacing()).equals((Object)helpingPos)) continue;
                        dumbHelpingRay = helpingRay;
                        this.setState(helpingPos);
                        dumbRay = RayTraceFactory.rayTrace((Entity)entity, helpingPos, facing.getOpposite(), HELPER, state, -1.0);
                        if (!dumbRay.getPos().offset(dumbRay.getFacing()).equals((Object)pos)) {
                            dumbRay = null;
                            dumbHelpingRay = null;
                        }
                        HELPER.delete(helpingPos);
                        continue;
                    }
                    this.setState(helpingPos);
                    ray = RayTraceFactory.rayTrace((Entity)entity, helpingPos, facing.getOpposite(), HELPER, state, -1.0);
                    if (ray == null || !ray.getPos().offset(ray.getFacing()).equals((Object)pos)) continue;
                    if (forceRay == null) {
                        forceRay = ray;
                        forceHelpingRay = helpingRay;
                    }
                    if ((ray.isLegit() || this.smartRay.getValue() != RayTraceMode.Smart && this.smartRay.getValue() != RayTraceMode.Force) && this.entityCheck(helpingPos)) {
                        this.placeBlock(helpingRay.getPos(), helpingRay.getFacing(), helpingRay.getRotations(), helpingRay.getResult().hitVec);
                        if (this.blocksPlaced >= (Integer)this.blocks.getValue() || this.noFastHelp(helpingPos, pos)) {
                            return true;
                        }
                        if (this.entityCheck(pos)) {
                            this.placeBlock(ray.getPos(), ray.getFacing(), ray.getRotations(), ray.getResult().hitVec);
                            this.setState(pos);
                            return this.blocksPlaced >= (Integer)this.blocks.getValue() || this.rotate.getValue() == Rotate.Normal;
                        }
                        return false;
                    }
                    HELPER.delete(helpingPos);
                }
            } else if ((ray.isLegit() || this.smartRay.getValue() != RayTraceMode.Smart && this.smartRay.getValue() != RayTraceMode.Force) && this.entityCheck(pos)) {
                this.setState(pos);
                this.placeBlock(ray.getPos(), ray.getFacing(), ray.getRotations(), ray.getResult().hitVec);
                return this.blocksPlaced >= (Integer)this.blocks.getValue() || this.rotate.getValue() == Rotate.Normal;
            }
            if (forceRay == null || !forceRay.getPos().offset(forceRay.getFacing()).equals((Object)pos)) {
                forceRay = dumbRay;
                forceHelpingRay = dumbHelpingRay;
            }
            if (this.smartRay.getValue() == RayTraceMode.Force && forceRay != null && forceRay.getPos().offset(forceRay.getFacing()).equals((Object)pos)) {
                BlockPos forcePos;
                if (forceHelpingRay != null) {
                    BlockPos helping = forceHelpingRay.getPos().offset(forceHelpingRay.getFacing());
                    if (this.entityCheck(helping)) {
                        this.placeBlock(forceHelpingRay.getPos(), forceHelpingRay.getFacing(), forceHelpingRay.getRotations(), forceHelpingRay.getResult().hitVec);
                        this.setState(helping);
                        if (this.blocksPlaced >= (Integer)this.blocks.getValue() || this.noFastHelp(helping, pos)) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                if (!this.entityCheck(forcePos = forceRay.getPos().offset(forceRay.getFacing()))) {
                    return false;
                }
                this.placeBlock(forceRay.getPos(), forceRay.getFacing(), forceRay.getRotations(), forceRay.getResult().hitVec);
                this.setState(forcePos);
                return this.blocksPlaced >= (Integer)this.blocks.getValue() || this.rotate.getValue() == Rotate.Normal;
            }
            return false;
        }
        EnumFacing initialFacing = BlockUtil.getFacing(pos, HELPER);
        if (this.shouldHelp(initialFacing, pos)) {
            BlockPos helpingPos = null;
            for (EnumFacing facing : EnumFacing.values()) {
                helpingPos = pos.offset(facing);
                EnumFacing helpingFacing = BlockUtil.getFacing(helpingPos, HELPER);
                if (helpingFacing == null || !this.entityCheck(helpingPos)) continue;
                initialFacing = facing;
                this.placeBlock(helpingPos.offset(helpingFacing), helpingFacing.getOpposite());
                this.setState(helpingPos);
                break;
            }
            if (this.blocksPlaced >= (Integer)this.blocks.getValue() || helpingPos != null && this.noFastHelp(helpingPos, pos)) {
                return true;
            }
        }
        if (initialFacing == null || !this.entityCheck(pos)) {
            return false;
        }
        this.placeBlock(pos.offset(initialFacing), initialFacing.getOpposite());
        this.setState(pos);
        return this.blocksPlaced >= (Integer)this.blocks.getValue() || this.rotate.getValue() == Rotate.Normal;
    }

    protected boolean shouldHelp(EnumFacing facing, BlockPos pos) {
        return facing == null;
    }

    public void setState(BlockPos pos) {
        Block block;
        Object object = block = this.slot <= 0 || this.slot > 8 ? Blocks.ENDER_CHEST : null;
        if (block == null) {
            Item item;
            Item item2 = item = this.slot == -2 ? ObbyModule.mc.player.getHeldItemOffhand().getItem() : ObbyModule.mc.player.inventory.getStackInSlot(this.slot).getItem();
            if (item instanceof ItemBlock) {
                block = ((ItemBlock)item).getBlock();
            }
        }
        if (block != null) {
            HELPER.addBlockState(pos, block.getDefaultState());
        }
    }

    protected boolean quickEntityCheck(BlockPos pos) {
        return ObbyModule.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).stream().anyMatch(e -> e != null && !EntityUtil.isDead(e) && e.preventEntitySpawning && (!(e instanceof EntityEnderCrystal) || this.attack.getValue() == false));
    }

    protected boolean noFastHelp(BlockPos helpingPos, BlockPos pos) {
        switch (this.fastHelpingBlocks.getValue()) {
            case Off: {
                return this.rotate.getValue() == Rotate.Normal;
            }
            case Down: {
                return this.rotate.getValue() == Rotate.Normal && !pos.down().equals((Object)helpingPos);
            }
        }
        return false;
    }

    static {
        Bus.EVENT_BUS.register(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MAX_VALUE){

            @Override
            public void invoke(MotionUpdateEvent event) {
                if (event.getStage() == Stage.POST) {
                    HELPER.clearAllStates();
                }
            }
        });
    }
}

