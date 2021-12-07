package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractTextCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class SayCommand
extends AbstractTextCommand
implements Globals {
    public SayCommand() {
        super("say");
        CommandDescriptions.register(this, "Use this command to say a message. This can be useful for macros.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cUse this command to send a chat message.\u00a7f (Useful for Macros)");
        } else {
            String message = CommandUtil.concatenate(args, 1);
            if (SayCommand.mc.player != null) {
                SayCommand.mc.player.sendChatMessage(message);
            } else {
                ChatUtil.sendMessage(message);
            }
        }
    }
}
