/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;

final class ListenerClickBlock
extends ModuleListener<Nuker, ClickBlockEvent> {
    public ListenerClickBlock(Nuker module) {
        super(module, ClickBlockEvent.class, 11);
    }

    @Override
    public void invoke(ClickBlockEvent event) {
        if (((Nuker)this.module).nuke.getValue().booleanValue() && ((Nuker)this.module).timer.passed(((Nuker)this.module).delay.getValue().intValue()) && !((Nuker)this.module).breaking) {
            ((Nuker)this.module).currentSelection = ((Nuker)this.module).getSelection(event.getPos());
            ((Nuker)this.module).breaking = true;
            ((Nuker)this.module).breakSelection(((Nuker)this.module).currentSelection, ((Nuker)this.module).autoTool.getValue());
            event.setCancelled(true);
        }
    }
}

