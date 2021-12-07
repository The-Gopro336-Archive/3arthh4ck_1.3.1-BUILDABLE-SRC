/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.velocity.ListenerBlockPush;
import me.earth.earthhack.impl.modules.movement.velocity.ListenerBobber;
import me.earth.earthhack.impl.modules.movement.velocity.ListenerEntityVelocity;
import me.earth.earthhack.impl.modules.movement.velocity.ListenerExplosion;
import me.earth.earthhack.impl.modules.movement.velocity.ListenerWaterPush;
import me.earth.earthhack.impl.modules.movement.velocity.VelocityData;
import me.earth.earthhack.impl.util.math.MathUtil;

public class Velocity
extends Module {
    protected final Setting<Boolean> knockBack = this.register(new BooleanSetting("KnockBack", true));
    protected final Setting<Float> horizontal = this.register(new NumberSetting<Float>("Horizontal", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Float> vertical = this.register(new NumberSetting<Float>("Vertical", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> noPush = this.register(new BooleanSetting("NoPush", true));
    protected final Setting<Boolean> explosions = this.register(new BooleanSetting("Explosions", true));
    protected final Setting<Boolean> bobbers = this.register(new BooleanSetting("Bobbers", true));
    protected final Setting<Boolean> water = this.register(new BooleanSetting("Water", false));
    protected final Setting<Boolean> blocks = this.register(new BooleanSetting("Blocks", false));
    protected final Setting<Boolean> shulkers = this.register(new BooleanSetting("Shulkers", false));

    public Velocity() {
        super("Velocity", Category.Movement);
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerEntityVelocity(this));
        this.listeners.add(new ListenerWaterPush(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerBobber(this));
        this.setData(new VelocityData(this));
    }

    @Override
    public String getDisplayInfo() {
        return "H" + MathUtil.round(this.horizontal.getValue().floatValue(), 1) + "%V" + MathUtil.round(this.vertical.getValue().floatValue(), 1) + "%";
    }
}

