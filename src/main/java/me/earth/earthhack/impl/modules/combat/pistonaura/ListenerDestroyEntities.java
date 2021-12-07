package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities
extends ModuleListener<PistonAura, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerDestroyEntities(PistonAura module) {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        if (((PistonAura)this.module).destroyEntities.getValue().booleanValue()) {
            for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
                if (id != ((PistonAura)this.module).entityId) continue;
                mc.addScheduledTask(() -> {
                    if (((PistonAura)this.module).current != null) {
                        ((PistonAura)this.module).current.setValid(false);
                    }
                });
            }
        }
    }
}
