/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import org.lwjgl.input.Keyboard;

final class ListenerKeyboard
extends ModuleListener<Arrows, KeyboardEvent> {
    public ListenerKeyboard(Arrows module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (((Arrows)this.module).cycleButton.getValue().getKey() == Keyboard.getEventKey() && event.getEventState()) {
            ((Arrows)this.module).cycle(false, false);
        }
    }
}

