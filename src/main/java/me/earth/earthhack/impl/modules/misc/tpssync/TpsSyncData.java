package me.earth.earthhack.impl.modules.misc.tpssync;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSync;

final class TpsSyncData
extends DefaultData<TpsSync> {
    public TpsSyncData(TpsSync module) {
        super(module);
        this.register("Attack", "Syncs your attacks with the tps.");
        this.register("Mine", "Syncs mining blocks with the tps.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Syncs your actions with the servers tps.";
    }
}
