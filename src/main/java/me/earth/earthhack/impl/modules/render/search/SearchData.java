package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.search.Search;

final class SearchData
extends DefaultData<Search> {
    public SearchData(Search module) {
        super(module);
        this.register(module.lines, "Draws outlines around found blocks.");
        this.register(module.fill, "Draws boxes on found blocks.");
        this.register(module.tracers, "Draws tracers to found blocks.");
        this.register(module.softReload, "Makes the world not flicker when new blocks are being loaded.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Search blocks in render distance.";
    }
}
