package me.earth.earthhack.impl.gui.chat.util;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;

final class ColorEnum$3
extends ColorEnum {
    ColorEnum$3(String textColor) {
    }

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
