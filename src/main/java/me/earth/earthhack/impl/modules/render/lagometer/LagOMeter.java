/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.earth.earthhack.impl.modules.render.lagometer;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeterData;
import me.earth.earthhack.impl.modules.render.lagometer.ListenerPosLook;
import me.earth.earthhack.impl.modules.render.lagometer.ListenerRender;
import me.earth.earthhack.impl.modules.render.lagometer.ListenerTeleport;
import me.earth.earthhack.impl.modules.render.lagometer.ListenerText;
import me.earth.earthhack.impl.modules.render.lagometer.ListenerTick;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.gui.ScaledResolution;

public class LagOMeter
extends BlockESPModule {
    protected final Setting<Boolean> esp;
    protected final Setting<Boolean> response;
    protected final Setting<Boolean> lagTime;
    protected final Setting<Boolean> nametag;
    protected final Setting<Float> scale;
    protected final ColorSetting textColor;
    protected final Setting<Integer> responseTime;
    protected final Setting<Integer> time;
    protected final Setting<Integer> chatTime;
    protected final Setting<Boolean> chat;
    protected final Setting<Boolean> render;
    protected final AtomicBoolean teleported;
    protected final StopWatch lag;
    protected ScaledResolution resolution;
    protected String respondingMessage;
    protected String lagMessage;
    protected boolean sent;
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;

    public LagOMeter() {
        super("Lag-O-Meter", Category.Render);
        this.esp = this.registerBefore(new BooleanSetting("ESP", true), this.color);
        this.response = this.registerBefore(new BooleanSetting("Response", true), this.color);
        this.lagTime = this.registerBefore(new BooleanSetting("Lag", true), this.color);
        this.nametag = this.register(new BooleanSetting("Nametag", false));
        this.scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(0.003f), Float.valueOf(0.001f), Float.valueOf(0.01f)));
        this.textColor = this.register(new ColorSetting("Name-Color", new Color(255, 255, 255, 255)));
        this.responseTime = this.register(new NumberSetting<Integer>("ResponseTime", 500, 0, 2500));
        this.time = this.register(new NumberSetting<Integer>("ESP-Time", 500, 0, 2500));
        this.chatTime = this.register(new NumberSetting<Integer>("Chat-Time", 3000, 0, 5000));
        this.chat = this.register(new BooleanSetting("Chat", true));
        this.render = this.register(new BooleanSetting("Render-Text", true));
        this.teleported = new AtomicBoolean();
        this.lag = new StopWatch();
        this.unregister(this.height);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTeleport(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerText(this));
        this.listeners.add(new ListenerTick(this));
        this.color.setValue(new Color(255, 0, 0, 80));
        this.outline.setValue(new Color(255, 0, 0, 255));
        this.setData(new LagOMeterData(this));
    }

    @Override
    public String getDisplayInfo() {
        long t = System.currentTimeMillis() - Managers.NCP.getTimeStamp();
        if (t > (long)this.time.getValue().intValue()) {
            return null;
        }
        return "\u00a7c" + MathUtil.round((double)t / 1000.0, 1);
    }

    @Override
    protected void onEnable() {
        this.sent = false;
        this.teleported.set(true);
        this.resolution = new ScaledResolution(mc);
        this.x = Managers.POSITION.getX();
        this.y = Managers.POSITION.getY();
        this.z = Managers.POSITION.getZ();
        this.yaw = Managers.ROTATION.getServerYaw();
        this.pitch = Managers.ROTATION.getServerPitch();
    }
}

