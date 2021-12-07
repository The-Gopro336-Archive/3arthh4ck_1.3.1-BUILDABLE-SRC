/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;

final class ListenerInput
extends ModuleListener<Scaffold, MovementInputEvent> {
    public ListenerInput(Scaffold module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (((Scaffold)this.module).down.getValue().booleanValue() && ((Scaffold)this.module).fastSneak.getValue().booleanValue() && ListenerInput.mc.gameSettings.keyBindSneak.isKeyDown() && !ListenerInput.mc.gameSettings.keyBindJump.isKeyDown()) {
            event.getInput().sneak = false;
            event.setCancelled(true);
        }
    }
}

