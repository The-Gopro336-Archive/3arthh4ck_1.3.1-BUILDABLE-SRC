/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.mcf;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;

final class MiddleClickListener
extends ModuleListener<MCF, ClickMiddleEvent> {
    public MiddleClickListener(MCF module) {
        super(module, ClickMiddleEvent.class);
    }

    @Override
    public void invoke(ClickMiddleEvent event) {
        if (event.isModuleCancelled()) {
            return;
        }
        ((MCF)this.module).onMiddleClick();
    }
}

