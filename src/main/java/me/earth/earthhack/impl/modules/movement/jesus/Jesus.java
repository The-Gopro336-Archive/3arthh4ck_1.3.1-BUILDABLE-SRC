/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.modules.movement.jesus.ListenerCollision;
import me.earth.earthhack.impl.modules.movement.jesus.ListenerLiquidJump;
import me.earth.earthhack.impl.modules.movement.jesus.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.jesus.ListenerTick;
import me.earth.earthhack.impl.modules.movement.jesus.ListenerWorldClient;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;

public class Jesus
extends Module
implements CollisionEvent.Listener {
    protected final Setting<JesusMode> mode = this.register(new EnumSetting<JesusMode>("Mode", JesusMode.Solid));
    protected final ListenerCollision listenerCollision;
    protected final StopWatch timer = new StopWatch();
    protected boolean jumped;

    public Jesus() {
        super("Jesus", Category.Movement);
        this.listenerCollision = new ListenerCollision(this);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerLiquidJump(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerTick(this));
        SimpleData data = new SimpleData(this, "Walk on water like Jesus.");
        data.register(this.mode, "-Solid just walk on water.\n-Trampoline makes you jump high on water.\n-Dolphin mini jumps.");
        this.setData(data);
    }

    @Override
    public void onCollision(CollisionEvent event) {
        if (this.isEnabled()) {
            this.listenerCollision.invoke(event);
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().toString();
    }
}

