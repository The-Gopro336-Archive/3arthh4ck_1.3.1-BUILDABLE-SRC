/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;

final class ListenerGuiOpen
extends ModuleListener<Auto32k, GuiScreenEvent<?>> {
    public ListenerGuiOpen(Auto32k module) {
        super(module, GuiScreenEvent.class);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        ((Auto32k)this.module).onGui(event);
    }
}

