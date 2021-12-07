/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SimpleSoundObserver;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ListenerRender;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ListenerRenderPos;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ListenerRotations;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ListenerTick;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ServerAutoCrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;

public class ServerAutoCrystal
extends SimpleSubModule<PingBypass> {
    protected final Setting<Boolean> soundR = this.register(new BooleanSetting("SoundRemove", false));
    protected final SoundObserver observer;
    protected final StopWatch timer = new StopWatch();
    protected BlockPos renderPos;

    public ServerAutoCrystal(PingBypass pingBypass) {
        super(pingBypass, "S-AutoCrystal", Category.Client);
        this.register(new BooleanSetting("Place", true));
        this.register(new EnumSetting<Target>("Target", Target.Closest));
        this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
        this.register(new NumberSetting<Integer>("PlaceDelay", 0, 0, 500));
        this.register(new NumberSetting<Float>("MaxSelfPlace", Float.valueOf(9.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
        this.register(new NumberSetting<Float>("FacePlace", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
        this.register(new NumberSetting<Integer>("MultiPlace", 1, 1, 5));
        this.register(new BooleanSetting("CountMin", true));
        this.register(new BooleanSetting("AntiSurround", true));
        this.register(new BooleanSetting("1.13+", false));
        this.register(new BooleanSetting("Break", true));
        this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new NumberSetting<Integer>("BreakDelay", 0, 0, 500));
        this.register(new NumberSetting<Float>("MaxSelfBreak", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
        this.register(new BooleanSetting("Instant", false));
        this.register(new EnumSetting<ACRotate>("Rotate", ACRotate.None));
        this.register(new BooleanSetting("MultiThread", false));
        this.register(new BooleanSetting("Suicide", false));
        this.register(new BooleanSetting("Stay", false));
        this.register(new NumberSetting<Float>("Range", Float.valueOf(12.0f), Float.valueOf(6.0f), Float.valueOf(12.0f)));
        this.register(new BooleanSetting("Override", false));
        this.register(new NumberSetting<Float>("MinFace", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(4.0f)));
        this.register(new BooleanSetting("AntiFriendPop", true));
        this.register(new NumberSetting<Integer>("Cooldown", 500, 0, 500));
        this.register(new BooleanSetting("MultiTask", true));
        this.register(new NumberSetting<Float>("CombinedTrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new BooleanSetting("FallBack", true));
        this.register(new NumberSetting<Float>("FB-Dmg", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
        this.register(new BooleanSetting("Tick", true));
        this.register(new BooleanSetting("SetDead", false));
        this.register(new NumberSetting<Integer>("ThreadDelay", 30, 0, 100));
        this.register(new BooleanSetting("Post-Tick", false));
        this.register(new BooleanSetting("Gameloop", false));
        this.register(new BooleanSetting("Packet", true));
        this.listeners.add(new ListenerRotations(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderPos(this));
        this.listeners.add(new ListenerTick(this));
        this.observer = new SimpleSoundObserver(this.soundR::getValue);
        this.setData(new ServerAutoCrystalData(this));
    }

    @Override
    protected void onEnable() {
        Managers.SET_DEAD.addObserver(this.observer);
    }

    @Override
    protected void onDisable() {
        Managers.SET_DEAD.removeObserver(this.observer);
    }

    protected void onTick() {
        if (!((PingBypass)this.getParent()).isEnabled()) {
            return;
        }
        if (this.timer.passed(1000L) || ServerAutoCrystal.mc.player == null || !InventoryUtil.isHolding(Items.END_CRYSTAL)) {
            this.renderPos = null;
            this.timer.reset();
        }
        if (ServerAutoCrystal.mc.player != null && InventoryUtil.isHolding(Items.END_CRYSTAL) && !InventoryUtil.isHoldingServer(Items.END_CRYSTAL) && ((PingBypass)this.getParent()).isEnabled()) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, InventoryUtil::syncItem);
        }
    }
}

