package me.earth.earthhack.impl.modules.render.penis;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.penis.Penis;

final class PenisData
extends DefaultData<Penis> {
    public PenisData(Penis module) {
        super(module);
        this.register(module.selfLength, "Self penis length.");
        this.register(module.friendLength, "Friend penis length.");
        this.register(module.enemyLength, "Enemy penis length.");
        this.register(module.uncircumcised, "Make pp uncircumcised");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Highlights Holes around you.";
    }
}
