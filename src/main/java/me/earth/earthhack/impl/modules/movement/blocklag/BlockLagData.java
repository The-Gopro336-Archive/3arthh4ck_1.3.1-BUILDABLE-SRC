package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;

final class BlockLagData
extends DefaultData<BlockLag> {
    public BlockLagData(BlockLag module) {
        super(module);
        this.register(module.vClip, "V-clips the specified amount down to cause a lagback. Don't touch, 9 should be perfect.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "The OG Burrow.";
    }
}
