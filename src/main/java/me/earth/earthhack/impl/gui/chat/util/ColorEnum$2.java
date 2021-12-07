package me.earth.earthhack.impl.gui.chat.util;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;

final class ColorEnum$2
extends ColorEnum {
    ColorEnum$2(String textColor) {
    }

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
