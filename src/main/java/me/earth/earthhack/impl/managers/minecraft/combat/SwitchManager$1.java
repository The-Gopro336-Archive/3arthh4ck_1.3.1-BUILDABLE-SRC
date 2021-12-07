package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketHeldItemChange;

class SwitchManager$1
extends EventListener<PacketEvent.Post<CPacketHeldItemChange>> {
    SwitchManager$1(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketHeldItemChange> event) {
        SwitchManager.this.timer.reset();
        SwitchManager.this.last_slot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
    }
}
