package me.earth.earthhack.impl.modules.player.speedmine.mode;

import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import net.minecraft.util.math.AxisAlignedBB;

final class ESPMode$4
extends ESPMode {
    @Override
    public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage) {
        Outline.drawEsp(module, bb, damage);
        Block.drawEsp(module, bb, damage);
    }
}
