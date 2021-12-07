package me.earth.earthhack.impl.modules.misc.extratab;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;

final class ExtraTabData
extends DefaultData<ExtraTab> {
    public ExtraTabData(ExtraTab module) {
        super(module);
        this.register(module.size, "How many players you want to display when pressing tab.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Extends the tab menu.";
    }
}
