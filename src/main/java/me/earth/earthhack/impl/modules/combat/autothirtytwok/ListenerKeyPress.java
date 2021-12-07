/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;

final class ListenerKeyPress
extends ModuleListener<Auto32k, KeyboardEvent> {
    public ListenerKeyPress(Auto32k module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        ((Auto32k)this.module).onKeyInput(event);
    }
}

