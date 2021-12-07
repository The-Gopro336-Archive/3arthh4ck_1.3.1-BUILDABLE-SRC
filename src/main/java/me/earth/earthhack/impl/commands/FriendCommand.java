/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractPlayerManagerCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;

public class FriendCommand
extends AbstractPlayerManagerCommand {
    public FriendCommand() {
        super(Managers.FRIENDS, "friend", "Friends", "friended", "a friend", "\u00a7b");
        CommandDescriptions.register(this, "Manage your friends.");
    }
}

