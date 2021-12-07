/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.modules.misc.autolog.util.LogScreen;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;

final class ListenerScreen
extends ModuleListener<AutoLog, GuiScreenEvent<GuiDisconnected>> {
    public ListenerScreen(AutoLog module) {
        super(module, GuiScreenEvent.class, GuiDisconnected.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiDisconnected> event) {
        if (((AutoLog)this.module).awaitScreen) {
            ((AutoLog)this.module).awaitScreen = false;
            mc.displayGuiScreen((GuiScreen)new LogScreen((AutoLog)this.module, ((AutoLog)this.module).message, ((AutoLog)this.module).serverData));
            event.setCancelled(true);
        }
    }
}

