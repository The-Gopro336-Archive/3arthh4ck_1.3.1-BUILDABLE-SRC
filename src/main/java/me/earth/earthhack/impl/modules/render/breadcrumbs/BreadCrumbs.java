package me.earth.earthhack.impl.modules.render.breadcrumbs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.breadcrumbs.ListenerDeath;
import me.earth.earthhack.impl.modules.render.breadcrumbs.ListenerLogout;
import me.earth.earthhack.impl.modules.render.breadcrumbs.ListenerRender;
import me.earth.earthhack.impl.modules.render.breadcrumbs.ListenerTick;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.render.ColorModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.Vec3d;

public class BreadCrumbs
extends ColorModule {
    public static final Vec3d ORIGIN = new Vec3d(8.0, 64.0, 8.0);
    protected final Setting<Boolean> render = this.register(new BooleanSetting("Render", true));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 0, 0, 10000));
    protected final Setting<Float> width = this.register(new NumberSetting<Float>("Width", Float.valueOf(1.6f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    protected final Setting<Integer> fadeDelay = this.register(new NumberSetting<Integer>("Fade-Delay", 2000, 0, 10000));
    protected final Setting<Boolean> clearD = this.register(new BooleanSetting("Death-Clear", true));
    protected final Setting<Boolean> clearL = this.register(new BooleanSetting("Logout-Clear", true));
    protected final Setting<Boolean> fade = this.register(new BooleanSetting("Fade", false));
    protected final Setting<Boolean> players = this.register(new BooleanSetting("Players", false));
    protected final StopWatch timer = new StopWatch();
    protected final List<Trace> positions = new ArrayList<Trace>();
    protected Trace trace;

    public BreadCrumbs() {
        super("BreadCrumbs", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerDeath(this));
        SimpleData data = new SimpleData(this, "Shows where you came from.");
        data.register(this.color, "The color the path will be rendered in.");
        data.register(this.render, "If the path should be rendered.");
        data.register(this.delay, "Intervals in which the BreadCrumbs aren't drawn.");
        data.register(this.fadeDelay, "Delay at which the breadcrumb fades away.");
        data.register(this.width, "Width of the rendered path.");
        data.register(this.clearD, "Clears the path when you die.");
        data.register(this.clearL, "Clears the path when you disconnect from the server.");
        data.register(this.fade, "Makes the breadcrumb fade away.");
        this.setData(data);
        this.color.setValue(new Color(255, 0, 0, 125));
    }

    @Override
    protected void onDisable() {
        this.positions.clear();
        this.trace = null;
    }
}
