package me.earth.earthhack.impl.modules.render.trails;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.modules.render.trails.ListenerDestroyEntities;
import me.earth.earthhack.impl.modules.render.trails.ListenerRender;
import me.earth.earthhack.impl.modules.render.trails.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.render.trails.ListenerTick;
import me.earth.earthhack.impl.util.animation.TimeAnimation;

public class Trails
extends Module {
    protected final Setting<Boolean> arrows = this.register(new BooleanSetting("Arrows", false));
    protected final Setting<Boolean> pearls = this.register(new BooleanSetting("Pearls", false));
    protected final Setting<Boolean> snowballs = this.register(new BooleanSetting("Snowballs", false));
    protected final Setting<Integer> time = this.register(new NumberSetting<Integer>("Time", 1, 1, 10));
    protected final ColorSetting color = this.register(new ColorSetting("Color", new Color(255, 0, 0, 255)));
    protected final Setting<Float> width = this.register(new NumberSetting<Float>("Width", Float.valueOf(1.6f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    protected Map<Integer, TimeAnimation> ids = new ConcurrentHashMap<Integer, TimeAnimation>();
    protected Map<Integer, List<Trace>> traceLists = new ConcurrentHashMap<Integer, List<Trace>>();
    protected Map<Integer, Trace> traces = new ConcurrentHashMap<Integer, Trace>();

    public Trails() {
        super("Trails", Category.Render);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerDestroyEntities(this));
    }

    @Override
    protected void onEnable() {
        this.ids = new ConcurrentHashMap<Integer, TimeAnimation>();
        this.traces = new ConcurrentHashMap<Integer, Trace>();
        this.traceLists = new ConcurrentHashMap<Integer, List<Trace>>();
    }
}
