/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;

final class ListenerToolTip
extends ModuleListener<ToolTips, ToolTipEvent> {
    public ListenerToolTip(ToolTips module) {
        super(module, ToolTipEvent.class);
    }

    @Override
    public void invoke(ToolTipEvent event) {
        if (((ToolTips)this.module).shulkers.getValue().booleanValue() && !event.isCancelled() && ((ToolTips)this.module).drawShulkerToolTip(event.getStack(), event.getX(), event.getY(), null)) {
            event.setCancelled(true);
        }
    }
}

