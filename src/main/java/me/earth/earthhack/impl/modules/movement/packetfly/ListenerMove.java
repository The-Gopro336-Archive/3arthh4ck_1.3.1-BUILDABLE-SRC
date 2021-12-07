/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;

final class ListenerMove
extends ModuleListener<PacketFly, MoveEvent> {
    public ListenerMove(PacketFly module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((PacketFly)this.module).mode.getValue() == Mode.Setback || ((PacketFly)this.module).teleportID.get() != 0) {
            if (((PacketFly)this.module).zeroSpeed.getValue().booleanValue()) {
                event.setX(0.0);
                event.setY(0.0);
                event.setZ(0.0);
            } else {
                event.setX(ListenerMove.mc.player.motionX);
                event.setY(ListenerMove.mc.player.motionY);
                event.setZ(ListenerMove.mc.player.motionZ);
            }
            if (((PacketFly)this.module).zeroY.getValue().booleanValue()) {
                event.setY(0.0);
            }
            if (((PacketFly)this.module).phase.getValue() == Phase.Semi || ((PacketFly)this.module).isPlayerCollisionBoundingBoxEmpty()) {
                ListenerMove.mc.player.noClip = true;
            }
        }
    }
}

