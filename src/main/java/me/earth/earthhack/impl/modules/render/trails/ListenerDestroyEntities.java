package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities
extends ModuleListener<Trails, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerDestroyEntities(Trails module) {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
            if (!((Trails)this.module).ids.containsKey(id)) continue;
            ((Trails)this.module).ids.get(id).play();
        }
    }
}
