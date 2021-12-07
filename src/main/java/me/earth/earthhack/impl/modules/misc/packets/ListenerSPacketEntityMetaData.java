/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntityMetadata
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.play.server.SPacketEntityMetadata;

final class ListenerSPacketEntityMetaData
extends ModuleListener<Packets, PacketEvent.Receive<SPacketEntityMetadata>> {
    public ListenerSPacketEntityMetaData(Packets module) {
        super(module, PacketEvent.Receive.class, SPacketEntityMetadata.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityMetadata> event) {
    }
}

