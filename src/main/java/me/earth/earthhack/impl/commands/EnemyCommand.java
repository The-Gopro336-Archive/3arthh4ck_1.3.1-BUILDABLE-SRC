package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractPlayerManagerCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;

public class EnemyCommand
extends AbstractPlayerManagerCommand {
    public EnemyCommand() {
        super(Managers.ENEMIES, "enemy", "Enemies", "enemied", "an enemy", "\u00a7c");
        CommandDescriptions.register(this, "Manage your enemies.");
    }
}
