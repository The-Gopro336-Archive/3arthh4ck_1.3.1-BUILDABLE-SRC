package me.earth.earthhack.impl.gui.chat.util;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;

final class ColorEnum$1
extends ColorEnum {
    ColorEnum$1(String textColor) {
    }

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
