/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDisconnected
 */
package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnect;
import net.minecraft.client.gui.GuiDisconnected;

final class ListenerScreen
extends ModuleListener<AutoReconnect, GuiScreenEvent<GuiDisconnected>> {
    public ListenerScreen(AutoReconnect module) {
        super(module, GuiScreenEvent.class, -1000, GuiDisconnected.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiDisconnected> event) {
        if (!event.isCancelled()) {
            ((AutoReconnect)this.module).onGuiDisconnected(event.getScreen());
            event.setCancelled(true);
        }
    }
}

