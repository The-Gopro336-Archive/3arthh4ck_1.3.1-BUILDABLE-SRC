/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.EnumConnectionState
 */
package me.earth.earthhack.impl.util.network;

import net.minecraft.network.EnumConnectionState;

public interface CustomPacket {
    public int getId() throws Exception;

    default public EnumConnectionState getState() {
        return EnumConnectionState.PLAY;
    }
}

