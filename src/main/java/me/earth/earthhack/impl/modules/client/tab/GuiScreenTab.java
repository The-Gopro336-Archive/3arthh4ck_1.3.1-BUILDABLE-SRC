package me.earth.earthhack.impl.modules.client.tab;

import java.io.IOException;
import me.earth.earthhack.impl.commands.gui.ExitButton;
import me.earth.earthhack.impl.modules.client.tab.TabModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenTab
extends GuiScreen {
    private final TabModule module;

    public GuiScreenTab(TabModule module) {
        this.module = module;
    }

    public boolean doesGuiPauseGame() {
        return this.module.pause.getValue();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.module.getBind().getKey()) {
            this.module.disable();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        this.buttonList.clear();
        this.buttonList.add(new ExitButton(0, this.width - 24, 5));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc.world == null) {
            this.drawDefaultBackground();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.module.disable();
        }
    }
}
