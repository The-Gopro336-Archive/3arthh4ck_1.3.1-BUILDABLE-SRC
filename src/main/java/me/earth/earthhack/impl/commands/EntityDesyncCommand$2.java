package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import net.minecraft.network.play.client.CPacketVehicleMove;

class EntityDesyncCommand$2
extends EventListener<TickEvent> {
    EntityDesyncCommand$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        if (Globals.mc.player != null && EntityDesyncCommand.this.dismounted != null) {
            if (Globals.mc.player.isRiding()) {
                EntityDesyncCommand.this.dismounted = null;
                return;
            }
            EntityDesyncCommand.this.dismounted.setPosition(Globals.mc.player.posX, Globals.mc.player.posY, Globals.mc.player.posZ);
            Globals.mc.player.connection.sendPacket(new CPacketVehicleMove(EntityDesyncCommand.this.dismounted));
        }
    }
}
