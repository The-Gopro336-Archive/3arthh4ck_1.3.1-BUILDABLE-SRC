package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.NumberComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;

final class NumberComponent$NumberComponentFactory<F extends Number>
implements IComponentFactory<F, NumberSetting<F>> {
    private NumberComponent$NumberComponentFactory() {
    }

    @Override
    public SettingComponent<F, NumberSetting<F>> create(NumberSetting<F> setting) {
        return new NumberComponent(setting);
    }
}
