/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.skinblink.ListenerGameLoop;
import me.earth.earthhack.impl.modules.misc.skinblink.SkinBlinkData;
import me.earth.earthhack.impl.util.math.StopWatch;

public class SkinBlink
extends Module {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 1000, 0, 2000));
    protected final Setting<Boolean> random = this.register(new BooleanSetting("Random", false));
    protected final StopWatch timer = new StopWatch();

    public SkinBlink() {
        super("SkinBlink", Category.Misc);
        this.listeners.add(new ListenerGameLoop(this));
        this.setData(new SkinBlinkData(this));
    }
}

