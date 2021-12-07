package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.impl.util.math.StopWatch;

class CombatManager$PopCounter {
    private final StopWatch timer = new StopWatch();
    private int pops;

    private CombatManager$PopCounter() {
    }

    public int getPops() {
        return this.pops;
    }

    public void pop() {
        this.timer.reset();
        ++this.pops;
    }

    public void reset() {
        this.pops = 0;
    }

    public long lastPop() {
        return this.timer.getTime();
    }
}
