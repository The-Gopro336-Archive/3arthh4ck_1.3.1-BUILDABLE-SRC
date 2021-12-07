package me.earth.earthhack.impl.modules.client.clickgui;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.modules.client.clickgui.ListenerScreen;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends Module {
    public final Setting<Color> color = this.register(new ColorSetting("Color", new Color(0, 80, 255)));
    public final Setting<Boolean> catEars = this.register(new BooleanSetting("CatEars", false));
    public final Setting<Boolean> blur = this.register(new BooleanSetting("Blur", false));
    public final Setting<Integer> blurAmount = this.register(new NumberSetting<Integer>("Blur-Amount", 8, 1, 20));
    public final Setting<Integer> blurSize = this.register(new NumberSetting<Integer>("Blur-Size", 3, 1, 20));
    public final Setting<String> open = this.register(new StringSetting("Open", "+"));
    public final Setting<String> close = this.register(new StringSetting("Close", "-"));
    public final Setting<Boolean> white = this.register(new BooleanSetting("White-Settings", true));
    public final Setting<Boolean> description = this.register(new BooleanSetting("Description", true));
    public final Setting<Integer> descriptionWidth = this.register(new NumberSetting<Integer>("Description-Width", 240, 100, 1000));
    protected boolean fromEvent;
    protected GuiScreen screen;

    public ClickGui() {
        super("ClickGui", Category.Client);
        this.listeners.add(new ListenerScreen(this));
        this.setData(new SimpleData(this, "Beautiful ClickGui by OHare"));
    }

    @Override
    protected void onEnable() {
        this.screen = ClickGui.mc.currentScreen;
        Click gui = new Click();
        gui.init();
        gui.onGuiOpened();
        mc.displayGuiScreen(gui);
    }

    @Override
    protected void onDisable() {
        if (!this.fromEvent) {
            mc.displayGuiScreen(this.screen);
        }
        this.fromEvent = false;
    }
}
