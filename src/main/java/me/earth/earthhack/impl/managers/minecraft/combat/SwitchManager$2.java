package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketHeldItemChange;

class SwitchManager$2
extends EventListener<PacketEvent.Receive<SPacketHeldItemChange>> {
    SwitchManager$2(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketHeldItemChange> event) {
        SwitchManager.this.last_slot = ((SPacketHeldItemChange)event.getPacket()).getHeldItemHotbarIndex();
    }
}
