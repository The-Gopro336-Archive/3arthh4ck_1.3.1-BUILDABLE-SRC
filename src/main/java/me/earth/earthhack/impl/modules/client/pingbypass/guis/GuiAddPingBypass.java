package me.earth.earthhack.impl.modules.client.pingbypass.guis;

import java.io.IOException;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class GuiAddPingBypass
extends GuiScreen {
    private static final SettingCache<String, StringSetting, PingBypass> IP = Caches.getSetting(PingBypass.class, StringSetting.class, "IP", "Proxy-IP");
    private static final SettingCache<String, StringSetting, PingBypass> PORT = Caches.getSetting(PingBypass.class, StringSetting.class, "Port", "0");
    private final GuiScreen parentScreen;
    private GuiTextField serverPortField;
    private GuiTextField serverIPField;

    public GuiAddPingBypass(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    public void updateScreen() {
        this.serverIPField.updateCursorCounter();
        this.serverPortField.updateCursorCounter();
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, "Done"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, "Cancel"));
        this.serverIPField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
        this.serverIPField.setFocused(true);
        this.serverIPField.setText(IP.getValue());
        this.serverPortField = new GuiTextField(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20);
        this.serverPortField.setMaxStringLength(128);
        this.serverPortField.setText(PORT.getValue());
        ((GuiButton)this.buttonList.get((int)0)).enabled = !this.serverPortField.getText().isEmpty() && this.serverPortField.getText().split(":").length > 0 && !this.serverIPField.getText().isEmpty();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 1) {
                this.parentScreen.confirmClicked(false, 1337);
            } else if (button.id == 0) {
                IP.computeIfPresent(s -> s.setValue(this.serverIPField.getText()));
                PORT.computeIfPresent(s -> s.setValue(this.serverPortField.getText()));
                this.parentScreen.confirmClicked(true, 1337);
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) {
        this.serverIPField.textboxKeyTyped(typedChar, keyCode);
        this.serverPortField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 15) {
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
            this.serverPortField.setFocused(!this.serverPortField.isFocused());
        }
        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        ((GuiButton)this.buttonList.get((int)0)).enabled = !this.serverPortField.getText().isEmpty() && this.serverPortField.getText().split(":").length > 0 && !this.serverIPField.getText().isEmpty();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverPortField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.func_73732_a(this.fontRenderer, "Edit PingBypass", this.width / 2, 17, 0xFFFFFF);
        this.func_73731_b(this.fontRenderer, "Proxy-IP", this.width / 2 - 100, 53, 0xA0A0A0);
        this.func_73731_b(this.fontRenderer, "Proxy-Port", this.width / 2 - 100, 94, 0xA0A0A0);
        this.serverIPField.drawTextBox();
        this.serverPortField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
