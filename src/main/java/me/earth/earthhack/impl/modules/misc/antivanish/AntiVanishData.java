package me.earth.earthhack.impl.modules.misc.antivanish;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.antivanish.AntiVanish;

final class AntiVanishData
extends DefaultData<AntiVanish> {
    public AntiVanishData(AntiVanish module) {
        super(module);
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Detects players that go into vanish.";
    }
}
