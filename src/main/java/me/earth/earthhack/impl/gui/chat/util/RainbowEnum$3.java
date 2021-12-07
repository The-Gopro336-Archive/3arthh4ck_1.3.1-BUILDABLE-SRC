package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.gui.chat.util.RainbowEnum;
import me.earth.earthhack.impl.util.math.MathUtil;

final class RainbowEnum$3
extends RainbowEnum {
    RainbowEnum$3(String range, String color) {
    }

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
}
