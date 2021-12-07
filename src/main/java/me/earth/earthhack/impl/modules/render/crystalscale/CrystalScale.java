/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.crystalscale;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.crystalscale.ListenerDestroyEntities;
import me.earth.earthhack.impl.modules.render.crystalscale.ListenerSpawnObject;
import me.earth.earthhack.impl.util.animation.TimeAnimation;

public class CrystalScale
extends Module {
    public final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    public final Setting<Boolean> animate = this.register(new BooleanSetting("Animate", false));
    public final Setting<Integer> time = this.register(new NumberSetting<Integer>("AnimationTime", 200, 1, 500));
    public final Map<Integer, TimeAnimation> scaleMap = new ConcurrentHashMap<Integer, TimeAnimation>();

    public CrystalScale() {
        super("CrystalScale", Category.Render);
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerSpawnObject(this));
    }
}

