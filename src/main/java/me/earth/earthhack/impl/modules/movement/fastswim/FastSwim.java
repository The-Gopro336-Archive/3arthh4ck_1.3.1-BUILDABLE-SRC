/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.fastswim.FastSwimData;
import me.earth.earthhack.impl.modules.movement.fastswim.ListenerMove;

public class FastSwim
extends Module {
    protected final Setting<Double> vWater = this.register(new NumberSetting<Double>("V-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> downWater = this.register(new NumberSetting<Double>("Down-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> hWater = this.register(new NumberSetting<Double>("H-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> vLava = this.register(new NumberSetting<Double>("Up-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Double> downLava = this.register(new NumberSetting<Double>("Down-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Double> hLava = this.register(new NumberSetting<Double>("H-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Boolean> strafe = this.register(new BooleanSetting("Strafe", false));
    protected final Setting<Boolean> fall = this.register(new BooleanSetting("Fall", false));
    protected final Setting<Boolean> accelerate = this.register(new BooleanSetting("Accelerate", false));
    protected final Setting<Double> accelerateFactor = this.register(new NumberSetting<Double>("Factor", 1.1, 0.1, 20.0));
    protected double lavaSpeed;
    protected double waterSpeed;

    public FastSwim() {
        super("FastSwim", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.setData(new FastSwimData(this));
    }
}

