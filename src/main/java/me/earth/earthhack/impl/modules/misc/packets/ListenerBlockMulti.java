/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketMultiBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange$BlockUpdateData
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti
extends ModuleListener<Packets, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerBlockMulti(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if (((Packets)this.module).fastBlockStates.getValue().booleanValue()) {
            SPacketMultiBlockChange p = (SPacketMultiBlockChange)event.getPacket();
            for (SPacketMultiBlockChange.BlockUpdateData d : p.getChangedBlocks()) {
                ((Packets)this.module).stateMap.put(d.getPos(), d.getBlockState());
            }
            mc.addScheduledTask(() -> {
                for (SPacketMultiBlockChange.BlockUpdateData d : p.getChangedBlocks()) {
                    ((Packets)this.module).stateMap.remove((Object)d.getPos());
                }
            });
        }
    }
}

