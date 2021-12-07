package me.earth.earthhack.impl.modules.client.pingbypass.guis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.text.TextComponentTranslation;

class GuiConnectingPingBypass$1
extends Thread {
    final String val$proxyIP;
    final int val$proxyPort;
    final String val$actualIP;
    final int val$actualPort;

    GuiConnectingPingBypass$1(String x0, String string, int n, String string2, int n2) {
        this.val$proxyIP = string;
        this.val$proxyPort = n;
        this.val$actualIP = string2;
        this.val$actualPort = n2;
        super(x0);
    }

    @Override
    public void run() {
        InetAddress inetaddress = null;
        try {
            if (GuiConnectingPingBypass.this.cancel) {
                return;
            }
            inetaddress = InetAddress.getByName(this.val$proxyIP);
            GuiConnectingPingBypass.this.networkManager = NetworkManager.createNetworkManagerAndConnect((InetAddress)inetaddress, (int)this.val$proxyPort, (boolean)GuiConnectingPingBypass.this.mc.gameSettings.isUsingNativeTransport());
            GuiConnectingPingBypass.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnectingPingBypass.this.networkManager, GuiConnectingPingBypass.this.mc, GuiConnectingPingBypass.this.previousGuiScreen));
            GuiConnectingPingBypass.this.networkManager.sendPacket(new C00Handshake(this.val$actualIP, this.val$actualPort, EnumConnectionState.LOGIN, true));
            GuiConnectingPingBypass.this.networkManager.sendPacket(new CPacketLoginStart(GuiConnectingPingBypass.this.mc.getSession().getProfile()));
        }
        catch (UnknownHostException e) {
            if (GuiConnectingPingBypass.this.cancel) {
                return;
            }
            LOGGER.error("Couldn't connect to PingBypass", e);
            GuiConnectingPingBypass.this.mc.addScheduledTask(() -> GuiConnectingPingBypass.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnectingPingBypass.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"}))));
        }
        catch (Exception exception) {
            if (GuiConnectingPingBypass.this.cancel) {
                return;
            }
            LOGGER.error("Couldn't connect to PingBypass", exception);
            String s = exception.toString();
            if (inetaddress != null) {
                String s1 = inetaddress + ":" + this.val$proxyPort;
                s = s.replace(s1, "");
            }
            String finalS = s;
            GuiConnectingPingBypass.this.mc.addScheduledTask(() -> GuiConnectingPingBypass.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnectingPingBypass.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{finalS}))));
        }
    }
}
