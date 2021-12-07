/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.commands;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.commands.Commands;

final class CommandData
extends DefaultData<Commands> {
    protected CommandData(Commands module) {
        super(module);
        this.register("Prefix", "The clients prefix. Send a chat message that starts with it to use the clients command system.");
        this.register("PrefixBind", "If this setting is enabled you can just click the button belonging to your prefix to open the chat with the prefix already filled in.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "A Module that manages commands for the Client. Just like with Managers it doesn't matter if this module is enabled or not.";
    }
}

