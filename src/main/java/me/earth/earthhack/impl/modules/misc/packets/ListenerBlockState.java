package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockState
extends ModuleListener<Packets, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockState(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if (((Packets)this.module).fastBlockStates.getValue().booleanValue()) {
            SPacketBlockChange p = (SPacketBlockChange)event.getPacket();
            ((Packets)this.module).stateMap.put(p.getBlockPosition(), p.getBlockState());
            mc.addScheduledTask(() -> ((Packets)this.module).stateMap.remove(p.getBlockPosition()));
        }
    }
}
