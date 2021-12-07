/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main.command;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.ICommandHandler;
import me.earth.earthhack.impl.modules.client.server.main.command.ICommandLineHandler;

public class CommandLineManager
implements ICommandLineHandler {
    private final Map<String, ICommandHandler> handlers = new HashMap<String, ICommandHandler>();

    @Override
    public void handle(String line) throws CommandException {
        String[] command = line.split(" ", 2);
        if (command.length < 1) {
            throw new CommandException("Your command was empty...");
        }
        ICommandHandler handler = this.handlers.get(command[0].toLowerCase());
        if (handler == null) {
            throw new CommandException("Unknown command: " + command[0] + ".");
        }
        handler.handle(command.length == 1 ? "" : command[1]);
    }

    @Override
    public void add(String command, ICommandHandler handler) {
        this.handlers.put(command.toLowerCase(), handler);
    }
}

