package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.flight.Flight;

final class FlightData
extends DefaultData<Flight> {
    public FlightData(Flight module) {
        super(module);
        this.register(module.mode, "-Normal normal flight\n-Creative fly like in creative mode\n-Jump uses jumps to fly\n-AAC a flight mode for the AAC anticheat.");
        this.register(module.speed, "The speed to fly with.");
        this.register(module.animation, "Trick the server into thinking you are standing on the ground.");
        this.register(module.damage, "Bypass that attempts to deal damage to you when enabling the module.");
        this.register(module.antiKick, "Slowly glides down to prevent you from getting kicked.");
        this.register(module.glide, "Glide down with the Glide-Speed.");
        this.register(module.glideSpeed, "Speed to glide down with while Glide is enabled.");
        this.register(module.aacY, "Vertical speed for the AAC mode.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Makes you fly.";
    }
}
