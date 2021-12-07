package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketDestroyEntities;

class SetDeadManager$2
extends EventListener<PacketEvent.Receive<SPacketDestroyEntities>> {
    SetDeadManager$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        Globals.mc.addScheduledTask(() -> {
            for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
                SetDeadManager.this.confirmKill(id);
            }
        });
    }
}
