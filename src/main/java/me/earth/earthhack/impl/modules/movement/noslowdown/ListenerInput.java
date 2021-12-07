/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MovementInput
 */
package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import net.minecraft.util.MovementInput;

final class ListenerInput
extends ModuleListener<NoSlowDown, MovementInputEvent> {
    public ListenerInput(NoSlowDown module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        MovementInput input = event.getInput();
        if (((NoSlowDown)this.module).items.getValue().booleanValue() && ((NoSlowDown)this.module).input.getValue().booleanValue() && input == ListenerInput.mc.player.movementInput && ListenerInput.mc.player.isHandActive() && !ListenerInput.mc.player.isRiding()) {
            input.moveStrafe /= 0.2f;
            input.moveForward /= 0.2f;
        }
    }
}

