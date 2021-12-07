package me.earth.earthhack.impl.modules.player.blink;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import net.minecraft.network.Packet;

final class ListenerPacket
extends ModuleListener<Blink, PacketEvent.Send<?>> {
    public ListenerPacket(Blink module) {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event) {
        if (((Blink)this.module).packetMode.getValue().shouldCancel((Packet<?>)event.getPacket())) {
            mc.addScheduledTask(() -> ((Blink)this.module).packets.add((Packet<?>)event.getPacket()));
            event.setCancelled(true);
        }
    }
}
