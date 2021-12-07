package me.earth.earthhack.impl.util.helpers.gui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiModule
extends Module {
    protected GuiScreen screen;
    protected boolean fromEvent;

    public GuiModule(String name, Category category) {
        super(name, category);
        this.listeners.add(new EventListener<GuiScreenEvent<?>>(GuiScreenEvent.class){

            @Override
            public void invoke(GuiScreenEvent<?> event) {
                GuiModule.this.onOtherGuiDisplayed();
            }
        });
    }

    @Override
    protected void onEnable() {
        this.screen = GuiModule.mc.currentScreen;
        this.display();
    }

    @Override
    protected void onDisable() {
        if (!this.fromEvent) {
            mc.displayGuiScreen(this.screen);
        }
        this.screen = null;
        this.fromEvent = false;
    }

    protected void onOtherGuiDisplayed() {
        this.fromEvent = true;
        this.disable();
    }

    protected void display() {
        mc.displayGuiScreen(this.provideScreen());
    }

    protected abstract GuiScreen provideScreen();
}
