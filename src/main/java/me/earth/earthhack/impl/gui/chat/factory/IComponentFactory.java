package me.earth.earthhack.impl.gui.chat.factory;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;

public interface IComponentFactory<E, S extends Setting<E>> {
    public SettingComponent<E, S> create(S var1);
}
