package me.earth.earthhack.impl.modules.misc.portals;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.portals.Portals;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

final class ListenerTeleport
extends ModuleListener<Portals, PacketEvent.Send<CPacketConfirmTeleport>> {
    public ListenerTeleport(Portals module) {
        super(module, PacketEvent.Send.class, CPacketConfirmTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketConfirmTeleport> event) {
        if (((Portals)this.module).godMode.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}
