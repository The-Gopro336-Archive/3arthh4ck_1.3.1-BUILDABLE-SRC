/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;

final class ListenerMove
extends ModuleListener<Scaffold, MoveEvent> {
    public ListenerMove(Scaffold module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (ListenerMove.mc.player.onGround) {
            event.setSneaking(((Scaffold)this.module).down.getValue() == false || !ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown() || ListenerMove.mc.gameSettings.keyBindJump.isKeyDown());
        }
    }
}

