package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAim;

final class AntiAimData
extends DefaultData<AntiAim> {
    public AntiAimData(AntiAim module) {
        super(module);
        this.register(module.strict, "Doesn't rotate when you place/attack.");
        this.register(module.skip, "Skips every Nth tick, off if value is 1.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "";
    }
}
