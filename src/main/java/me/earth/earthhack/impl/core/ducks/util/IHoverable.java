package me.earth.earthhack.impl.core.ducks.util;

public interface IHoverable {
    default public boolean canBeHovered() {
        return true;
    }
}
