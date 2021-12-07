/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.helpers.disabling;

public interface IDisablingModule {
    public void onShutDown();

    public void onDisconnect();

    public void onDeath();
}

