package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.EnumComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;

final class EnumComponent$EnumComponentFactory<F extends Enum<F>>
implements IComponentFactory<F, EnumSetting<F>> {
    private EnumComponent$EnumComponentFactory() {
    }

    @Override
    public SettingComponent<F, EnumSetting<F>> create(EnumSetting<F> setting) {
        return new EnumComponent<F>(setting);
    }
}
