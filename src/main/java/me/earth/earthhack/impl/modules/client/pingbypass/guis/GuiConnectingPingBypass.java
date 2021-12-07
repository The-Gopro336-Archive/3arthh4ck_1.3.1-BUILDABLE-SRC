/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.ServerAddress
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.network.NetHandlerLoginClient
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.network.EnumConnectionState
 *  net.minecraft.network.INetHandler
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.Packet
 *  net.minecraft.network.handshake.client.C00Handshake
 *  net.minecraft.network.login.client.CPacketLoginStart
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.impl.modules.client.pingbypass.guis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.util.text.IdleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiConnectingPingBypass
extends GuiScreen {
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SettingCache<String, StringSetting, PingBypass> IP = Caches.getSetting(PingBypass.class, StringSetting.class, "IP", "Proxy-IP");
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private NetworkManager networkManager;
    private boolean cancel;
    private final GuiScreen previousGuiScreen;

    public GuiConnectingPingBypass(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn) {
        this.mc = mcIn;
        this.previousGuiScreen = parent;
        mcIn.loadWorld(null);
        mcIn.setServerData(serverDataIn);
        ServerAddress serveraddress = ServerAddress.fromString((String)serverDataIn.serverIP);
        this.connect(IP.getValue(), PINGBYPASS.returnIfPresent(PingBypass::getPort, 25565), serveraddress.getIP(), serveraddress.getPort());
    }

    private void connect(final String proxyIP, final int proxyPort, final String actualIP, final int actualPort) {
        LOGGER.info("Connecting to PingBypass: {}, {}", (Object)proxyIP, (Object)proxyPort);
        new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()){

            @Override
            public void run() {
                InetAddress inetaddress = null;
                try {
                    if (GuiConnectingPingBypass.this.cancel) {
                        return;
                    }
                    inetaddress = InetAddress.getByName(proxyIP);
                    GuiConnectingPingBypass.this.networkManager = NetworkManager.createNetworkManagerAndConnect((InetAddress)inetaddress, (int)proxyPort, (boolean)GuiConnectingPingBypass.this.mc.gameSettings.isUsingNativeTransport());
                    GuiConnectingPingBypass.this.networkManager.setNetHandler((INetHandler)new NetHandlerLoginClient(GuiConnectingPingBypass.this.networkManager, GuiConnectingPingBypass.this.mc, GuiConnectingPingBypass.this.previousGuiScreen));
                    GuiConnectingPingBypass.this.networkManager.sendPacket((Packet)new C00Handshake(actualIP, actualPort, EnumConnectionState.LOGIN, true));
                    GuiConnectingPingBypass.this.networkManager.sendPacket((Packet)new CPacketLoginStart(GuiConnectingPingBypass.this.mc.getSession().getProfile()));
                }
                catch (UnknownHostException e) {
                    if (GuiConnectingPingBypass.this.cancel) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to PingBypass", (Throwable)e);
                    GuiConnectingPingBypass.this.mc.addScheduledTask(() -> GuiConnectingPingBypass.this.mc.displayGuiScreen((GuiScreen)new GuiDisconnected(GuiConnectingPingBypass.this.previousGuiScreen, "connect.failed", (ITextComponent)new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"}))));
                }
                catch (Exception exception) {
                    if (GuiConnectingPingBypass.this.cancel) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to PingBypass", (Throwable)exception);
                    String s = exception.toString();
                    if (inetaddress != null) {
                        String s1 = inetaddress + ":" + proxyPort;
                        s = s.replace(s1, "");
                    }
                    String finalS = s;
                    GuiConnectingPingBypass.this.mc.addScheduledTask(() -> GuiConnectingPingBypass.this.mc.displayGuiScreen((GuiScreen)new GuiDisconnected(GuiConnectingPingBypass.this.previousGuiScreen, "connect.failed", (ITextComponent)new TextComponentTranslation("disconnect.genericReason", new Object[]{finalS}))));
                }
            }
        }.start();
    }

    public void updateScreen() {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            } else {
                this.networkManager.handleDisconnection();
            }
        }
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format((String)"gui.cancel", (Object[])new Object[0])));
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.cancel = true;
            if (this.networkManager != null) {
                this.networkManager.closeChannel((ITextComponent)new TextComponentString("Aborted"));
            }
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (this.networkManager == null) {
            this.drawCenteredString(this.fontRenderer, "Authentication" + IdleUtil.getDots(), this.width / 2 + IdleUtil.getDots().length(), this.height / 2 - 50, 0xFFFFFF);
        } else {
            this.drawCenteredString(this.fontRenderer, "Loading PingBypass" + IdleUtil.getDots(), this.width / 2 + IdleUtil.getDots().length(), this.height / 2 - 50, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

