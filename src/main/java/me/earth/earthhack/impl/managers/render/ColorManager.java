/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.render;

import java.awt.Color;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

public class ColorManager
extends SubscriberImpl {
    private final Setting<Integer> speed = new NumberSetting<Integer>("RainbowSpeed", 50, 0, 100);
    private final Setting<Color> color = new ColorSetting("Color", new Color(127, 66, 186));
    private Color universal = new Color(255, 255, 255, 255);
    private float hue;

    public ColorManager() {
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
                ColorManager.this.update();
            }
        });
    }

    private void update() {
        if (this.speed.getValue() == 0) {
            return;
        }
        this.hue = (float)(System.currentTimeMillis() % (long)(360 * this.speed.getValue())) / (360.0f * (float)this.speed.getValue().intValue());
    }

    public void setUniversal(Color color) {
        this.universal = color;
    }

    public Color getUniversal() {
        return this.universal;
    }

    public float getHue() {
        return this.hue;
    }

    public float getHueByPosition(double pos) {
        return (float)((double)this.hue - pos * (double)0.001f);
    }

    public Setting<Integer> getRainbowSpeed() {
        return this.speed;
    }

    public Setting<Color> getColorSetting() {
        return this.color;
    }
}

