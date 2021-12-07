package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;

final class AutoLogData
extends DefaultData<AutoLog> {
    public AutoLogData(AutoLog module) {
        super(module);
        this.register(module.health, "Disconnects if your health goes below this value and you have less or equals totems than specified by the Totem setting.");
        this.register(module.totems, "If you have less totems than this and your health goes below the health setting you will be disconnected.");
        this.register(module.enemy, "If no enemy inside this range can be found you won't be disconnected.");
        this.register(module.absorption, "Takes Absorption into account when calculating your health.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Disconnects you automatically when in danger.";
    }
}
