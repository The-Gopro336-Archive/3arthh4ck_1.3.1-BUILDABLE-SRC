/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main;

import java.io.IOException;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.api.SimpleConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.SimplePacketManager;
import me.earth.earthhack.impl.modules.client.server.host.Host;
import me.earth.earthhack.impl.modules.client.server.main.BaseCommandLineHandler;
import me.earth.earthhack.impl.modules.client.server.main.SUnsupportedHandler;
import me.earth.earthhack.impl.modules.client.server.main.SystemShutdownHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.GlobalMessageHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.MessageHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.NameHandler;
import me.earth.earthhack.impl.modules.client.server.util.SystemLogger;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        int port = 0;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        int t = (int)((double)Runtime.getRuntime().availableProcessors() / 1.5) - 1;
        SystemLogger logger = new SystemLogger();
        SimplePacketManager pManager = new SimplePacketManager();
        SimpleConnectionManager cManager = new SimpleConnectionManager(pManager, t);
        pManager.add(0, new NameHandler(logger));
        pManager.add(2, new MessageHandler(logger));
        pManager.add(4, new GlobalMessageHandler(logger, cManager));
        for (int id : Protocol.ids()) {
            if (pManager.getHandlerFor(id) != null) continue;
            pManager.add(id, new SUnsupportedHandler("This is a command-line server. This type of packet is not supported!"));
        }
        Host host = Host.createAndStart(GlobalExecutor.FIXED_EXECUTOR, cManager, new SystemShutdownHandler(), port, true);
        System.out.println("Listening on port: " + host.getPort() + ". Enter \"exit\" or \"stop\" to exit.");
        BaseCommandLineHandler commandLine = new BaseCommandLineHandler(host);
        commandLine.startListening();
    }
}

