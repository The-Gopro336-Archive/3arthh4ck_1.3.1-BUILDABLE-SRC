package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.holetp.HoleTP;

final class HoleTPData
extends DefaultData<HoleTP> {
    public HoleTPData(HoleTP module) {
        super(module);
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
