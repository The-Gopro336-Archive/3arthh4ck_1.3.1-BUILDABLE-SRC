package me.earth.earthhack.impl.modules.misc.portals;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.portals.Portals;

final class PortalsData
extends DefaultData<Portals> {
    public PortalsData(Portals module) {
        super(module);
        this.register(module.godMode, "Never use outside a portal. Step through a portal, when you come out on the other side you can't move, but you also can't be damaged.");
        this.register("Chat", "Allows you to chat while being inside portals.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Tweaks for portals.";
    }
}
