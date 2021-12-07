/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main.command;

import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.ICommandHandler;

public interface ICommandLineHandler {
    public void handle(String var1) throws CommandException;

    public void add(String var1, ICommandHandler var2);
}

