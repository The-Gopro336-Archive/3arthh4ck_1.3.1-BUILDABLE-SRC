/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.IColorIncrementor;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.util.math.MathUtil;

public enum RainbowEnum implements IColorIncrementor
{
    RainbowSpeed("<0 - 200>", "\u00a7f"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            float speed = (float)IncrementationUtil.crF(s.getRainbowSpeed(), 0.0, 200.0, !i);
            return () -> {
                s.setRainbowSpeed(speed);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s) {
            return MathUtil.round(s.getRainbowSpeed(), 2) + "";
        }
    }
    ,
    RainbowSaturation("<0 - 100>", "\u00a76"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            float sat = (float)IncrementationUtil.crF(s.getRainbowSaturation(), 0.0, 100.0, !i);
            return () -> {
                s.setRainbowSaturation(sat);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s) {
            return MathUtil.round(s.getRainbowSaturation(), 2) + "";
        }
    }
    ,
    RainbowBrightness("<0 - 100>", "\u00a7f"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            float bright = (float)IncrementationUtil.crF(s.getRainbowBrightness(), 0.0, 100.0, !i);
            return () -> {
                s.setRainbowBrightness(bright);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s) {
            return MathUtil.round(s.getRainbowBrightness(), 2) + "";
        }
    };

    private final String range;
    private final String color;

    private RainbowEnum(String range, String color) {
        this.range = range;
        this.color = color;
    }

    public abstract String getValue(ColorSetting var1);

    public String getRange() {
        return this.range;
    }

    public String getColor() {
        return this.color;
    }
}

