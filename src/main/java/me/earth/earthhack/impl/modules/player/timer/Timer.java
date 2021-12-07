package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.timer.ListenerMotion;
import me.earth.earthhack.impl.modules.player.timer.ListenerPlayerPackets;
import me.earth.earthhack.impl.modules.player.timer.ListenerPosLook;
import me.earth.earthhack.impl.modules.player.timer.TimerData;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;

public class Timer
extends DisablingModule {
    protected final Setting<TimerMode> mode = this.register(new EnumSetting<TimerMode>("Mode", TimerMode.Normal));
    protected final Setting<Integer> autoOff = this.register(new NumberSetting<Integer>("AutoOff", 0, 0, 1000));
    protected final Setting<Integer> lagTime = this.register(new NumberSetting<Integer>("LagTime", 250, 0, 1000));
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(1.0888f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Integer> updates = this.register(new NumberSetting<Integer>("Updates", 2, 0, 100));
    protected final Setting<Float> fast = this.register(new NumberSetting<Float>("Fast", Float.valueOf(20.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Integer> fastTime = this.register(new NumberSetting<Integer>("FastTime", 100, 0, 5000));
    protected final Setting<Float> slow = this.register(new NumberSetting<Float>("Slow", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Integer> slowTime = this.register(new NumberSetting<Integer>("SlowTime", 250, 0, 5000));
    protected final Setting<Integer> maxPackets = this.register(new NumberSetting<Integer>("Max-Packets", 100, 0, 1000));
    protected final Setting<Integer> offset = this.register(new NumberSetting<Integer>("Offset", 10, 0, 100));
    protected final Setting<Integer> letThrough = this.register(new NumberSetting<Integer>("Network-Ticks", 10, 0, 100));
    protected final StopWatch offTimer = new StopWatch();
    protected final StopWatch switchTimer = new StopWatch();
    protected float pSpeed = 1.0f;
    protected int ticks = 0;
    protected int packets = 0;
    protected int sent = 0;
    protected boolean isSlow;

    public Timer() {
        super("Timer", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.addAll(new ListenerPlayerPackets(this).getListeners());
        this.setData(new TimerData(this));
    }

    @Override
    protected void onEnable() {
        this.packets = 0;
        this.sent = 0;
        this.isSlow = false;
        this.offTimer.reset();
    }

    @Override
    protected void onDisable() {
        Managers.TIMER.reset();
    }

    @Override
    public String getDisplayInfo() {
        String color = !Managers.NCP.passed(this.lagTime.getValue()) ? "\u00a7c" : "";
        switch (this.mode.getValue()) {
            case Switch: {
                return color + this.getSwitchSpeed();
            }
            case Physics: {
                return color + "Physics";
            }
            case Blink: {
                return (this.packets > 0 && this.pSpeed != 1.0f ? "\u00a7a" : color) + this.packets;
            }
        }
        return color + this.speed.getValue().toString();
    }

    public float getSpeed() {
        if (Managers.NCP.passed(this.lagTime.getValue())) {
            switch (this.mode.getValue()) {
                case Switch: {
                    if (this.switchTimer.passed(this.getTime())) {
                        this.isSlow = !this.isSlow;
                        this.switchTimer.reset();
                    }
                    return this.getSwitchSpeed();
                }
                case Normal: {
                    return this.speed.getValue().floatValue();
                }
                case Blink: {
                    return this.pSpeed;
                }
            }
        }
        return 1.0f;
    }

    private int getTime() {
        return this.isSlow ? this.slowTime.getValue().intValue() : this.fastTime.getValue().intValue();
    }

    private float getSwitchSpeed() {
        return this.isSlow ? this.slow.getValue().floatValue() : this.fast.getValue().floatValue();
    }
}
