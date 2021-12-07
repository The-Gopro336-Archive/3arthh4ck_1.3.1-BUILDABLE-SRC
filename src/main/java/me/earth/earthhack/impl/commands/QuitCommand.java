package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;

public class QuitCommand
extends Command
implements Globals {
    public QuitCommand() {
        super(new String[][]{{"quit"}});
    }

    @Override
    public void execute(String[] args) {
        mc.shutdown();
    }
}
