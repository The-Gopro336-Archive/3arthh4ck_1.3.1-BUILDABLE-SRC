package me.earth.earthhack.impl.modules.client.tab;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.modules.client.tab.GuiScreenTab;
import me.earth.earthhack.impl.util.helpers.gui.GuiModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class TabModule
extends GuiModule {
    protected final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> pause = this.register(new BooleanSetting("Pause", true));
    protected boolean isSilent;

    public TabModule() {
        super("Tab", Category.Client);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
                if (Globals.mc.currentScreen == null && TabModule.this.isSilent) {
                    Mouse.setGrabbed(false);
                }
            }
        });
    }

    @Override
    protected void onEnable() {
        this.isSilent = this.silent.getValue();
        if (!this.isSilent) {
            super.onEnable();
        }
    }

    @Override
    protected void onDisable() {
        if (!this.isSilent) {
            super.onDisable();
        }
    }

    @Override
    protected void onOtherGuiDisplayed() {
        if (!this.isSilent) {
            super.onOtherGuiDisplayed();
        }
    }

    @Override
    protected GuiScreen provideScreen() {
        return new GuiScreenTab(this);
    }
}
