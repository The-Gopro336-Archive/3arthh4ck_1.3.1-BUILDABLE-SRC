package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import me.earth.earthhack.impl.util.helpers.render.data.BlockESPModuleData;

final class LagOMeterData
extends BlockESPModuleData<LagOMeter> {
    public LagOMeterData(LagOMeter module) {
        super(module);
        this.register(module.esp, "Displays an ESP.");
        this.register(module.response, "Displays a warning when the server is lagging.");
        this.register(module.lagTime, "Displays a warning you got lagbacked.");
        this.register(module.nametag, "Displays a small Nametag at the ESP.");
        this.register(module.scale, "Scale of the Nametag.");
        this.register(module.textColor, "Color of the Nametag.");
        this.register(module.responseTime, "Time in ms the server has been lagging for before a warning is displayed.");
        this.register(module.time, "The Lag-ESP will be shown for this time.");
        this.register(module.chat, "Displays warnings in chat.");
        this.register(module.chatTime, "Time to display the Text-Warning for.");
        this.register(module.render, "Displays the warnings on top of your screen.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Shows lag and the server position.";
    }
}
