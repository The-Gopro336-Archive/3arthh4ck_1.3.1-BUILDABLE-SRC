/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketUnloadChunk
 */
package me.earth.earthhack.impl.modules.render.newchunks;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.newchunks.NewChunks;
import net.minecraft.network.play.server.SPacketUnloadChunk;

final class ListenerUnload
extends ModuleListener<NewChunks, PacketEvent.Receive<SPacketUnloadChunk>> {
    public ListenerUnload(NewChunks module) {
        super(module, PacketEvent.Receive.class, SPacketUnloadChunk.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketUnloadChunk> event) {
    }
}

