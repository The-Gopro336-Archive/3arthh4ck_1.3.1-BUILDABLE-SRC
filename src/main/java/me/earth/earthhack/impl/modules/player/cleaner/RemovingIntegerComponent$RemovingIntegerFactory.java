package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingInteger;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingIntegerComponent;

final class RemovingIntegerComponent$RemovingIntegerFactory
implements IComponentFactory<Integer, RemovingInteger> {
    private RemovingIntegerComponent$RemovingIntegerFactory() {
    }

    @Override
    public SettingComponent<Integer, RemovingInteger> create(RemovingInteger setting) {
        return new RemovingIntegerComponent(setting);
    }
}
