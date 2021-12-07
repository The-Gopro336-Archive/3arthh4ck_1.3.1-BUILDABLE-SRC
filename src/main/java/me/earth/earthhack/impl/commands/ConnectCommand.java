/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiMultiplayer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.GuiConnecting
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.multiplayer.ServerList
 *  net.minecraft.client.resources.I18n
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.I18n;

public class ConnectCommand
extends Command
implements Globals,
CommandScheduler {
    private ServerList cachedServerList;
    private long lastCache;

    public ConnectCommand() {
        super(new String[][]{{"connect"}, {"ip"}});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.sendMessage("\u00a7cPlease specify an IP!");
            return;
        }
        ServerUtil.disconnectFromMC("Disconnecting.");
        SCHEDULER.submit(() -> mc.addScheduledTask(() -> mc.displayGuiScreen((GuiScreen)new GuiConnecting((GuiScreen)new GuiMultiplayer((GuiScreen)new GuiMainMenu()), mc, new ServerData(I18n.format((String)"selectServer.defaultName", (Object[])new Object[0]), args[1], false)))), 100);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (this.cachedServerList == null || System.currentTimeMillis() - this.lastCache > 60000L) {
            this.cachedServerList = new ServerList(mc);
            this.cachedServerList.loadServerList();
            this.lastCache = System.currentTimeMillis();
        }
        if (args.length == 2) {
            for (int i = 0; i < this.cachedServerList.countServers(); ++i) {
                ServerData data = this.cachedServerList.getServerData(i);
                if (data.serverIP == null || !TextUtil.startsWith(data.serverIP, args[1])) continue;
                return new PossibleInputs(TextUtil.substring(data.serverIP, args[1].length()), "");
            }
        }
        if (args.length >= 2) {
            return PossibleInputs.empty();
        }
        return super.getPossibleInputs(args);
    }
}

