package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.esp.ESP;

final class ESPData
extends DefaultData<ESP> {
    public ESPData(ESP module) {
        super(module);
        this.register(module.mode, "Currently no other than Outline modes are supported.");
        this.register(module.players, "Render the ESP for players.");
        this.register(module.mode, "Render the ESP for Monsters.");
        this.register(module.animals, "Render the ESP for Animals.");
        this.register(module.vehicles, "Render the ESP for Vehicles.");
        this.register(module.misc, "Render the ESP for other entities.");
        this.register(module.items, "Render the ESP for item names.");
        this.register(module.storage, "Draw an ESP for storages.");
        this.register(module.lineWidth, "Line width for the Outline-ESP.");
        this.register(module.hurt, "Turn the ESP dark when the entity is hurt.");
        this.register(module.color, "Select the color for the esp.");
        this.register(module.invisibleColor, "Select the color for invisible players for the esp.");
        this.register(module.friendColor, "Select the color for friends for the esp.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Highlights Players and Entities through walls.";
    }
}
