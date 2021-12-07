package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketEntityAction;

class ActionManager$1
extends EventListener<PacketEvent.Post<CPacketEntityAction>> {
    ActionManager$1(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketEntityAction> event) {
        switch (((CPacketEntityAction)event.getPacket()).getAction()) {
            case START_SPRINTING: {
                ActionManager.this.sprinting = true;
                break;
            }
            case STOP_SPRINTING: {
                ActionManager.this.sprinting = false;
                break;
            }
            case START_SNEAKING: {
                ActionManager.this.sneaking = true;
                break;
            }
            case STOP_SNEAKING: {
                ActionManager.this.sneaking = false;
                break;
            }
        }
    }
}
