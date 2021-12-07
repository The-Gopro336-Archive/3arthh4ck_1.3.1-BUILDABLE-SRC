/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerBlockChange;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerClick;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerDamage;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerDeath;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerDigging;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerKeyPress;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerLogout;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerMotion;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerMultiBlockChange;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerRender;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerReset;
import me.earth.earthhack.impl.modules.player.speedmine.ListenerUpdate;
import me.earth.earthhack.impl.modules.player.speedmine.SpeedMineData;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Speedmine
extends Module {
    private static final ModuleCache<AutoMine> AUTO_MINE = Caches.getModule(AutoMine.class);
    protected final Setting<MineMode> mode = this.register(new EnumSetting<MineMode>("Mode", MineMode.Smart));
    protected final Setting<Boolean> noReset = this.register(new BooleanSetting("Reset", true));
    public final Setting<Float> limit = this.register(new NumberSetting<Float>("Damage", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(7.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> multiTask = this.register(new BooleanSetting("MultiTask", false));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> event = this.register(new BooleanSetting("Event", false));
    protected final Setting<Boolean> display = this.register(new BooleanSetting("DisplayDamage", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("ClickDelay", 100, 0, 500));
    protected final Setting<ESPMode> esp = this.register(new EnumSetting<ESPMode>("ESP", ESPMode.Outline));
    protected final Setting<Integer> alpha = this.register(new NumberSetting<Integer>("BlockAlpha", 100, 0, 255));
    protected final Setting<Integer> outlineA = this.register(new NumberSetting<Integer>("OutlineAlpha", 100, 0, 255));
    protected final Setting<Integer> realDelay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    public final Setting<Boolean> onGround = this.register(new BooleanSetting("OnGround", false));
    protected final Setting<Boolean> toAir = this.register(new BooleanSetting("ToAir", false));
    protected final Setting<Boolean> swap = this.register(new BooleanSetting("SilentSwitch", false));
    protected final Setting<Boolean> requireBreakSlot = this.register(new BooleanSetting("RequireBreakSlot", false));
    protected final Setting<Boolean> placeCrystal = this.register(new BooleanSetting("PlaceCrystal", false));
    protected final BindSetting breakBind = this.register(new BindSetting("BreakBind", Bind.none()));
    protected final Setting<Boolean> normal = this.register(new BooleanSetting("Normal", false));
    protected final Setting<Boolean> resetAfterPacket = this.register(new BooleanSetting("ResetAfterPacket", false));
    protected final Setting<Boolean> checkPacket = this.register(new BooleanSetting("CheckPacket", true));
    protected final Setting<Boolean> swingStop = this.register(new BooleanSetting("Swing-Stop", true));
    protected final Setting<Boolean> limitRotations = this.register(new BooleanSetting("Limit-Rotations", true));
    protected final Setting<Integer> confirm = this.register(new NumberSetting<Integer>("Confirm", 500, 0, 1000));
    public final float[] damages = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    protected final StopWatch timer = new StopWatch();
    protected final StopWatch resetTimer = new StopWatch();
    protected BlockPos pos;
    protected EnumFacing facing;
    protected AxisAlignedBB bb;
    protected float[] rotations;
    public float maxDamage;
    protected boolean sentPacket;
    protected boolean shouldAbort;
    protected boolean pausing;
    protected final StopWatch delayTimer = new StopWatch();
    protected Packet<?> limitRotationPacket;
    protected int limitRotationSlot = -1;

    public Speedmine() {
        super("Speedmine", Category.Player);
        this.listeners.add(new ListenerDamage(this));
        this.listeners.add(new ListenerReset(this));
        this.listeners.add(new ListenerClick(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.setData(new SpeedMineData(this));
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    public String getDisplayInfo() {
        if (this.display.getValue().booleanValue() && this.mode.getValue() == MineMode.Smart) {
            return this.maxDamage >= this.limit.getValue().floatValue() ? "\u00a7a" + MathUtil.round(this.limit.getValue().floatValue(), 1) : "" + MathUtil.round(this.maxDamage, 1);
        }
        return this.mode.getValue().toString();
    }

    public void abortCurrentPos() {
        AUTO_MINE.computeIfPresent(a -> a.addToBlackList(this.pos));
        Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
        ((IPlayerControllerMP)Speedmine.mc.playerController).setIsHittingBlock(false);
        ((IPlayerControllerMP)Speedmine.mc.playerController).setCurBlockDamageMP(0.0f);
        Speedmine.mc.world.sendBlockBreakProgress(Speedmine.mc.player.getEntityId(), this.pos, -1);
        Speedmine.mc.player.resetCooldown();
        this.reset();
    }

    public void reset() {
        this.pos = null;
        this.facing = null;
        this.bb = null;
        this.maxDamage = 0.0f;
        this.sentPacket = false;
        this.limitRotationSlot = -1;
        this.limitRotationPacket = null;
        AUTO_MINE.computeIfPresent(AutoMine::reset);
        for (int i = 0; i < 9; ++i) {
            this.damages[i] = 0.0f;
        }
    }

    public MineMode getMode() {
        return this.mode.getValue();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public StopWatch getTimer() {
        return this.timer;
    }

    public float getRange() {
        return this.range.getValue().floatValue();
    }

    public int getBlockAlpha() {
        return this.alpha.getValue();
    }

    public int getOutlineAlpha() {
        return this.outlineA.getValue();
    }

    public boolean isPausing() {
        return this.pausing;
    }

    public void setPausing(boolean pausing) {
        this.pausing = pausing;
    }

    protected boolean sendStopDestroy(BlockPos pos, EnumFacing facing, boolean toAir) {
        CPacketPlayerDigging stop = new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing);
        if (toAir) {
            ((ICPacketPlayerDigging)stop).setClientSideBreaking(true);
        }
        if (this.rotate.getValue().booleanValue() && this.limitRotations.getValue().booleanValue() && !RotationUtil.isLegit(pos, facing)) {
            this.limitRotationPacket = stop;
            this.limitRotationSlot = Speedmine.mc.player.inventory.currentItem;
            return false;
        }
        if (this.event.getValue().booleanValue()) {
            Speedmine.mc.player.connection.sendPacket((Packet)stop);
        } else {
            NetworkUtil.sendPacketNoEvent(stop, false);
        }
        this.onSendPacket();
        return true;
    }

    protected void postSend(boolean toAir) {
        if (this.swingStop.getValue().booleanValue()) {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
        if (toAir) {
            Speedmine.mc.playerController.onPlayerDestroyBlock(this.pos);
        }
        if (this.resetAfterPacket.getValue().booleanValue()) {
            this.reset();
        }
    }

    public void forceSend() {
        if (this.pos != null) {
            if (this.mode.getValue() == MineMode.Instant) {
                Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, this.facing));
                this.sendStopDestroy(this.pos, this.facing, false);
                if (this.mode.getValue() == MineMode.Instant) {
                    Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
                }
            } else if (this.mode.getValue() == MineMode.Civ) {
                this.sendStopDestroy(this.pos, this.facing, false);
            }
        }
    }

    public void tryBreak() {
        int breakSlot;
        if (!this.pausing && ((breakSlot = this.findBreakSlot()) != -1 || this.requireBreakSlot.getValue().booleanValue())) {
            boolean toAir = this.toAir.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = Speedmine.mc.player.inventory.currentItem;
                if (breakSlot != -1) {
                    InventoryUtil.switchTo(breakSlot);
                }
                CPacketPlayerDigging packet = new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, this.facing);
                if (toAir) {
                    ((ICPacketPlayerDigging)packet).setClientSideBreaking(true);
                }
                NetworkUtil.sendPacketNoEvent(packet, false);
                if (breakSlot != -1) {
                    InventoryUtil.switchTo(lastSlot);
                }
            });
            if (toAir) {
                Speedmine.mc.playerController.onPlayerDestroyBlock(this.pos);
            }
            this.onSendPacket();
        }
    }

    private int findBreakSlot() {
        int slot = -1;
        for (int i = 0; i < this.damages.length; ++i) {
            if (!(this.damages[i] >= this.limit.getValue().floatValue()) || (slot = i) < Speedmine.mc.player.inventory.currentItem) continue;
            return slot;
        }
        return slot;
    }

    public void checkReset() {
        if (this.sentPacket && this.resetTimer.passed(this.confirm.getValue().intValue()) && (this.mode.getValue() == MineMode.Packet || this.mode.getValue() == MineMode.Smart)) {
            this.reset();
        }
    }

    public void onSendPacket() {
        this.sentPacket = true;
        this.resetTimer.reset();
    }
}

