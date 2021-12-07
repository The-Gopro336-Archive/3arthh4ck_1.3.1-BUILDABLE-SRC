/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.crosshair;

import java.awt.Color;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.CrosshairEvent;
import me.earth.earthhack.impl.modules.render.crosshair.ListenerRender;
import me.earth.earthhack.impl.modules.render.crosshair.mode.GapMode;
import me.earth.earthhack.impl.util.client.SimpleData;

public class Crosshair
extends Module {
    protected final Setting<Boolean> indicator = this.register(new BooleanSetting("Attack-Indicator", true));
    protected final Setting<Boolean> outline = this.register(new BooleanSetting("Outline", true));
    protected final Setting<GapMode> gapMode = this.register(new EnumSetting<GapMode>("Gap-Mode", GapMode.NORMAL));
    protected final Setting<Color> color = this.register(new ColorSetting("Color", new Color(190, 60, 190)));
    protected final Setting<Color> outlineColor = this.register(new ColorSetting("Outline-Color", new Color(0, 0, 0)));
    protected final Setting<Float> length = this.register(new NumberSetting<Float>("Length", Float.valueOf(5.5f), Float.valueOf(0.5f), Float.valueOf(50.0f)));
    protected final Setting<Float> gapSize = this.register(new NumberSetting<Float>("Gap-Size", Float.valueOf(2.0f), Float.valueOf(0.5f), Float.valueOf(20.0f)));
    protected final Setting<Float> width = this.register(new NumberSetting<Float>("Width", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(10.0f)));

    public Crosshair() {
        super("Crosshair", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new EventListener<CrosshairEvent>(CrosshairEvent.class){

            @Override
            public void invoke(CrosshairEvent event) {
                event.setCancelled(true);
            }
        });
        SimpleData data = new SimpleData(this, "Gives you a custom crosshair.");
        this.setData(data);
    }
}

