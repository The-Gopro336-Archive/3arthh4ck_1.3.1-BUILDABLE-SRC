/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.spammer;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.spammer.Spammer;

final class ListenerUpdate
extends ModuleListener<Spammer, UpdateEvent> {
    public ListenerUpdate(Spammer module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (((Spammer)this.module).timer.passed(((Spammer)this.module).delay.getValue() * 1000)) {
            ListenerUpdate.mc.player.sendChatMessage((((Spammer)this.module).greenText.getValue() != false ? ">" : "") + ((Spammer)this.module).getSuffixedMessage());
            if (((Spammer)this.module).autoOff.getValue().booleanValue()) {
                ((Spammer)this.module).disable();
            }
            ((Spammer)this.module).timer.reset();
        }
    }
}

