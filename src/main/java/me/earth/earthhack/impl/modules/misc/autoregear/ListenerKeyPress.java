/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autoregear.AutoRegear;

final class ListenerKeyPress
extends ModuleListener<AutoRegear, KeyboardEvent> {
    public ListenerKeyPress(AutoRegear module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getKey() == ((AutoRegear)this.module).regear.getValue().getKey()) {
            ((AutoRegear)this.module).shouldRegear = true;
        }
    }
}

