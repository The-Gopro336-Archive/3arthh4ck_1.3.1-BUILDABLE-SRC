package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityRemoteAttack {
    default public boolean shouldRemoteAttack() {
        return false;
    }
}
