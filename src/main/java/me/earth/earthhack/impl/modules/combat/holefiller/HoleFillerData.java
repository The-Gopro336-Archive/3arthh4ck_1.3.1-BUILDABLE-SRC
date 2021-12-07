package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.impl.modules.combat.holefiller.HoleFiller;
import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

final class HoleFillerData
extends BlockPlacingData<HoleFiller> {
    public HoleFillerData(HoleFiller module) {
        super(module);
        this.register(module.range, "Holes will be filled within this range.");
        this.register(module.disable, "Automatically disables this module after it ran for the given time in milliseconds. If the delay is 0 the Module won't turn off automatically.");
        this.register(module.longHoles, "If 2x1 holes should be filled.");
        this.register(module.bigHoles, "If 2x2 holes should be filled.");
        this.register(module.calcDelay, "For weak CPUs, calculates Holes less frequently.");
        this.register(module.requireTarget, "HoleFiller is only active when a player is in range.");
        this.register(module.targetRange, "Only players within this range will be targeted.");
        this.register(module.targetDistance, "Minimum Distance from closest player to block.");
        this.register(module.minSelf, "If you are not in a hole, holes that are closer to you then this value will not be filled.");
        this.register(module.safety, "Takes Safety into account when using MaxSelf.");
        this.register(module.waitForHoleLeave, "Waits until the closest target leaves its hole.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Fills holes around you.";
    }
}
