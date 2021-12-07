package me.earth.earthhack.impl.gui.chat.util;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.IColorIncrementor;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;

public enum ColorEnum implements IColorIncrementor
{
    Red("\u00a7c"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            int red = s.getRed();
            if (red == 0 && !i || red == 255 && i) {
                return () -> {};
            }
            red = (int)IncrementationUtil.crL(red, 0L, 255L, !i);
            Color c = new Color(red, s.getGreen(), s.getBlue(), s.getAlpha());
            return () -> {
                s.setValue(c);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s) {
            return s.getRed();
        }
    }
    ,
    Green("\u00a7a"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            int green = s.getGreen();
            if (green == 0 && !i || green == 255 && i) {
                return () -> {};
            }
            green = (int)IncrementationUtil.crL(green, 0L, 255L, !i);
            Color c = new Color(s.getRed(), green, s.getBlue(), s.getAlpha());
            return () -> {
                s.setValue(c);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s) {
            return s.getGreen();
        }
    }
    ,
    Blue("\u00a79"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            int blue = s.getBlue();
            if (blue == 0 && !i || blue == 255 && i) {
                return () -> {};
            }
            blue = (int)IncrementationUtil.crL(blue, 0L, 255L, !i);
            Color c = new Color(s.getRed(), s.getGreen(), blue, s.getAlpha());
            return () -> {
                s.setValue(c);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s) {
            return s.getBlue();
        }
    }
    ,
    Alpha("\u00a7f"){

        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m) {
            int alpha = s.getAlpha();
            if (alpha == 0 && !i || alpha == 255 && i) {
                return () -> {};
            }
            alpha = (int)IncrementationUtil.crL(alpha, 0L, 255L, !i);
            Color c = new Color(s.getRed(), s.getGreen(), s.getBlue(), alpha);
            return () -> {
                s.setValue(c);
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s) {
            return s.getAlpha();
        }
    };

    private final String textColor;

    private ColorEnum(String textColor) {
        this.textColor = textColor;
    }

    public abstract int getValue(ColorSetting var1);

    public String getTextColor() {
        return this.textColor;
    }
}
