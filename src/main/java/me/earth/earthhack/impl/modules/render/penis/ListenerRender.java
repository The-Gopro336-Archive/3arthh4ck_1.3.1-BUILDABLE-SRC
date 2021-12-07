/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.penis;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.penis.Penis;

final class ListenerRender
extends ModuleListener<Penis, Render3DEvent> {
    public ListenerRender(Penis module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        ((Penis)this.module).onRender3D();
    }
}

