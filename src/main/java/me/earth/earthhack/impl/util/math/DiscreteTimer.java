package me.earth.earthhack.impl.util.math;

import me.earth.earthhack.impl.util.math.Passable;

public interface DiscreteTimer
extends Passable {
    public DiscreteTimer reset(long var1);

    public long getTime();
}
