package me.earth.earthhack.impl.modules.client.clickgui;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;

final class ListenerScreen
extends ModuleListener<ClickGui, GuiScreenEvent<?>> {
    public ListenerScreen(ClickGui module) {
        super(module, GuiScreenEvent.class);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        if (ListenerScreen.mc.currentScreen instanceof Click) {
            ((ClickGui)this.module).fromEvent = true;
            ((ClickGui)this.module).disable();
        }
    }
}
