package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntityStatus;

class CombatManager$2
extends EventListener<PacketEvent.Receive<SPacketEntityStatus>> {
    CombatManager$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
        switch (((SPacketEntityStatus)event.getPacket()).getOpCode()) {
            case 3: {
                Globals.mc.addScheduledTask(() -> CombatManager.this.onDeath(Globals.mc.world == null ? null : ((SPacketEntityStatus)event.getPacket()).getEntity(Globals.mc.world)));
                break;
            }
            case 35: {
                Globals.mc.addScheduledTask(() -> CombatManager.this.onTotemPop((SPacketEntityStatus)event.getPacket()));
            }
        }
    }
}
