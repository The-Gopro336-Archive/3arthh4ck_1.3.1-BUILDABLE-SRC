package me.earth.earthhack.impl.util.helpers.gui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;

class GuiModule$1
extends EventListener<GuiScreenEvent<?>> {
    GuiModule$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        GuiModule.this.onOtherGuiDisplayed();
    }
}
