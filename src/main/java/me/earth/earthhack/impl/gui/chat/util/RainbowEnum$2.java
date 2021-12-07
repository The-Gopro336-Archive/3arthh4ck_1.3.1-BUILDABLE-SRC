package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.gui.chat.util.RainbowEnum;
import me.earth.earthhack.impl.util.math.MathUtil;

final class RainbowEnum$2
extends RainbowEnum {
    RainbowEnum$2(String range, String color) {
    }

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
