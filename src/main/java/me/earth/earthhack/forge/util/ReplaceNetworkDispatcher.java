/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketJoinGame
 *  net.minecraftforge.fml.common.network.handshake.NetworkDispatcher
 */
package me.earth.earthhack.forge.util;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class ReplaceNetworkDispatcher
extends NetworkDispatcher {
    public ReplaceNetworkDispatcher(NetworkManager manager) {
        super(manager);
    }

    public int getOverrideDimension(SPacketJoinGame packetIn) {
        return packetIn.getDimension();
    }
}

