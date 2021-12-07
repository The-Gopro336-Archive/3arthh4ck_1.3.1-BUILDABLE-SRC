package me.earth.earthhack.impl.gui.chat.util;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;

final class ColorEnum$4
extends ColorEnum {
    ColorEnum$4(String textColor) {
    }

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
}
