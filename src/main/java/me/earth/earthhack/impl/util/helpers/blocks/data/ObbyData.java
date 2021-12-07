package me.earth.earthhack.impl.util.helpers.blocks.data;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

public class ObbyData<T extends ObbyModule>
extends BlockPlacingData<T> {
    public ObbyData(T module) {
        super(module);
        this.register("Attack", "Attacks Crystals blocking your positions, only recommended for Ping Players.");
        this.register("Pop", "- None : never pop when attacking\n- Time : Risky, probably won't pop you\n- Always : Safety over everything, Pops you to place blocks.");
        this.register("Cooldown", "Cooldown before attacking, see AutoCrystal - Cooldown");
        this.register("AntiWeakness", "Will perform a fast switch to a weakness breaking item, when attacking and when cooldown is 0.");
        this.register("BreakDelay", "Delay to attack Crystals with.");
        this.register("Fast-Helping", "\n-Off will not cause illegitimate rotations.\n-Down might cause illegitimate Rotations when placing downwards.\n-Fast might cause illegitimate Rotations.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "A module that places blocks, specialized on Obby.";
    }
}
