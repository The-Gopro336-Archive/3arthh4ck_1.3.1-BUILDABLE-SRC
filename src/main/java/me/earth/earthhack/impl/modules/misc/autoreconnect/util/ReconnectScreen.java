/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiMultiplayer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.GuiConnecting
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.resources.I18n
 */
package me.earth.earthhack.impl.modules.misc.autoreconnect.util;

import java.io.IOException;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.gui.util.IGuiDisconnected;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class ReconnectScreen
extends GuiDisconnected {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private final StopWatch timer = new StopWatch();
    private final IGuiDisconnected parent;
    private final ServerData data;
    private final int delay;
    private GuiButton reconnectButton;
    private boolean noData;
    private boolean reconnect;
    private long time;

    public ReconnectScreen(IGuiDisconnected parent, ServerData serverData, int delay) {
        super(parent.getParentScreen(), parent.getReason(), parent.getMessage());
        this.parent = parent;
        this.data = serverData;
        this.delay = delay;
        this.reconnect = true;
        this.time = System.currentTimeMillis();
        this.mc = Minecraft.getMinecraft();
        this.timer.reset();
    }

    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        int textHeight = ((IGuiDisconnected)((Object)this)).getMultilineMessage().size() * this.fontRenderer.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), (this.data == null ? "\u00a7c" : "\u00a7f") + "Reconnect"));
        this.reconnectButton = new GuiButton(2, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 23, this.getButtonString());
        this.buttonList.add(this.reconnectButton);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 46, I18n.format((String)"gui.toMenu", (Object[])new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            this.connect();
        } else if (button.id == 2) {
            this.reconnect = !this.reconnect;
            this.time = this.timer.getTime();
            this.reconnectButton.displayString = this.getButtonString();
        }
    }

    public void updateScreen() {
        if (!this.reconnect) {
            this.timer.setTime(System.currentTimeMillis() - this.time);
        }
        if (this.noData) {
            if (this.timer.passed(3000L)) {
                this.mc.displayGuiScreen((GuiScreen)new GuiMultiplayer((GuiScreen)new GuiMainMenu()));
            }
        } else if (this.timer.passed(this.delay) && this.reconnect) {
            this.connect();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        String text = this.getReconnectString();
        Managers.TEXT.drawStringWithShadow(text, (float)this.width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f, 16.0f, -1);
    }

    private void connect() {
        ServerData serverData;
        ServerData serverData2 = serverData = this.data == null ? this.mc.getCurrentServerData() : this.data;
        if (serverData != null) {
            if (PINGBYPASS.isEnabled()) {
                this.mc.displayGuiScreen((GuiScreen)new GuiConnectingPingBypass(this.parent.getParentScreen(), this.mc, serverData));
            } else {
                this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this.parent.getParentScreen(), this.mc, serverData));
            }
        } else {
            this.noData = true;
            this.timer.reset();
        }
    }

    private String getButtonString() {
        return "AutoReconnect: " + (this.reconnect ? "\u00a7aOn" : "\u00a7cOff");
    }

    private String getReconnectString() {
        float time = MathUtil.round((float)((long)this.delay - (this.reconnect ? this.timer.getTime() : this.time)) / 1000.0f, 1);
        return this.noData ? "\u00a7cNo ServerData found!" : "Reconnecting in " + (time <= 0.0f ? "0.0" : Float.valueOf(time)) + "s.";
    }
}

