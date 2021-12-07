/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;

final class ListenerMove
extends ModuleListener<NoSlowDown, MoveEvent> {
    public ListenerMove(NoSlowDown module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (Managers.NCP.passed(250) && ((IEntity)ListenerMove.mc.player).inWeb()) {
            if (ListenerMove.mc.player.onGround) {
                event.setX(event.getX() * ((NoSlowDown)this.module).websXZ.getValue());
                event.setZ(event.getZ() * ((NoSlowDown)this.module).websXZ.getValue());
            } else if (ListenerMove.mc.player.movementInput.sneak || !((NoSlowDown)this.module).sneak.getValue().booleanValue()) {
                event.setY(event.getY() * ((NoSlowDown)this.module).websY.getValue());
            }
        }
    }
}

