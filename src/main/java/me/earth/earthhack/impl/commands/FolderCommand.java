/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class FolderCommand
extends Command {
    public FolderCommand() {
        super(new String[][]{{"folder"}});
        CommandDescriptions.register(this, "Opens the 3arthh4ck folder.");
    }

    @Override
    public void execute(String[] args) {
        try {
            Desktop.getDesktop().open(Paths.get("earthhack", new String[0]).toFile());
        }
        catch (IOException e) {
            ChatUtil.sendMessage("\u00a7cAn error occurred.");
            e.printStackTrace();
        }
    }
}

