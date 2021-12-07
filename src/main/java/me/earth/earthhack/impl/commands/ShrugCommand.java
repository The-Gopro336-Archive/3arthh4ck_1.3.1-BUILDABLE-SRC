package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class ShrugCommand
extends Command
implements Globals {
    public static final String SHRUG = "\u00af\\_(\u30c4)_/\u00af";

    public ShrugCommand() {
        super(new String[][]{{"shrug"}, {"message"}});
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            this.sendMessage(SHRUG);
            return;
        }
        String message = CommandUtil.concatenate(args, 1);
        if (!message.contains(":shrug:")) {
            ChatUtil.sendMessage("\u00a7cUse :shrug: to specify parts of the message that should be replaced with the shrug emoji!");
            return;
        }
        this.sendMessage(message.replace(":shrug:", SHRUG));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length > 1) {
            return PossibleInputs.empty();
        }
        return super.getPossibleInputs(args);
    }

    private void sendMessage(String message) {
        if (ShrugCommand.mc.player == null) {
            ChatUtil.sendMessage(message);
        } else {
            ShrugCommand.mc.player.sendChatMessage(message);
        }
    }
}
