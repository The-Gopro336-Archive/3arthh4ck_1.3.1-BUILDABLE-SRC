package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.gui.chat.util.RainbowEnum;
import me.earth.earthhack.impl.util.math.MathUtil;

final class RainbowEnum$1
extends RainbowEnum {
    RainbowEnum$1(String range, String color) {
    }

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
