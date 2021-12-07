package me.earth.earthhack.impl.modules.misc.autolog.util;

import java.io.IOException;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

public class LogScreen
extends GuiScreen {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private final AutoLog autoLog;
    private final ServerData data;
    private final String message;
    private GuiButton autoLogButton;
    private final int textHeight;

    public LogScreen(AutoLog autoLog, String message, ServerData data) {
        this.autoLog = autoLog;
        this.mc = Minecraft.getMinecraft();
        this.message = message;
        this.data = data;
        this.textHeight = this.mc.fontRenderer.FONT_HEIGHT;
    }

    protected void keyTyped(char typedChar, int keyCode) {
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.mc.fontRenderer.FONT_HEIGHT, this.height - 30), (this.data == null ? "\u00a7c" : "\u00a7f") + "Reconnect"));
        this.autoLogButton = new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 23, this.getButtonString());
        this.buttonList.add(this.autoLogButton);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 46, "Back to server list"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && this.data != null) {
            if (PINGBYPASS.isEnabled()) {
                this.mc.displayGuiScreen(new GuiConnectingPingBypass(new GuiMainMenu(), this.mc, this.data));
            } else {
                this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, this.data));
            }
        } else if (button.id == 1) {
            this.autoLog.toggle();
            this.autoLogButton.displayString = this.getButtonString();
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.func_73732_a(this.fontRenderer, this.message, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getButtonString() {
        return "AutoLog: " + (this.autoLog.isEnabled() ? "\u00a7aOn" : "\u00a7cOff");
    }
}
