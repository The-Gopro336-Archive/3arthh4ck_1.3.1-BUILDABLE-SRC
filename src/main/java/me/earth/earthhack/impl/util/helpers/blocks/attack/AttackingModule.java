package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;

public interface AttackingModule {
    public Pop getPop();

    public int getPopTime();

    public double getRange();

    public double getTrace();
}
