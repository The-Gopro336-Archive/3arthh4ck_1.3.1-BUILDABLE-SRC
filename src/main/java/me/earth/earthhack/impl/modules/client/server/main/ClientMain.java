/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main;

import java.io.IOException;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.api.SimplePacketManager;
import me.earth.earthhack.impl.modules.client.server.api.SimpleServerList;
import me.earth.earthhack.impl.modules.client.server.client.Client;
import me.earth.earthhack.impl.modules.client.server.main.BaseCommandLineHandler;
import me.earth.earthhack.impl.modules.client.server.main.CUnsupportedHandler;
import me.earth.earthhack.impl.modules.client.server.main.command.handlers.MessageCommand;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.MessageHandler;
import me.earth.earthhack.impl.modules.client.server.util.SystemLogger;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Ip and port and name are missing!");
        }
        String ip = args[1];
        int port = Integer.parseInt(args[2]);
        SystemLogger log = new SystemLogger();
        log.log("Attempting to connect to: " + ip + ", " + port);
        SimplePacketManager manager = new SimplePacketManager();
        manager.add(2, new MessageHandler(log));
        manager.add(4, new MessageHandler(log, s -> "global: " + s));
        manager.add(6, new MessageHandler(log, s -> "error: " + s));
        manager.add(1, new MessageHandler(log, s -> "command: " + s));
        for (int id : Protocol.ids()) {
            if (manager.getHandlerFor(id) != null) continue;
            manager.add(id, new CUnsupportedHandler(log, id));
        }
        SimpleServerList serverList = new SimpleServerList();
        Client client = new Client(manager, serverList, ip, port);
        GlobalExecutor.EXECUTOR.submit(client);
        log.log("Client connected. Enter \"exit\" or \"stop\" to exit.");
        log.log("Setting name to " + args[3] + "...");
        client.setName(args[3]);
        client.send(ProtocolUtil.writeString(0, args[3]));
        BaseCommandLineHandler commands = new BaseCommandLineHandler(client);
        commands.add("msg", new MessageCommand(client, 2));
        commands.add("message", new MessageCommand(client, 2));
        commands.add("name", new MessageCommand(client, 0));
        commands.add("global", new MessageCommand(client, 4));
        commands.startListening();
    }
}

