package me.earth.earthhack.impl.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

public class HistoryCommand
extends Command
implements Globals {
    public HistoryCommand() {
        super(new String[][]{{"history"}, {"name"}});
        CommandDescriptions.register(this, "Gets the Namehistory of players.");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cPlease specify a name.");
        } else if (args.length > 1) {
            Managers.CHAT.sendDeleteMessage("\u00a7bLooking up \u00a7f" + args[1] + "'s " + "\u00a7b" + "name history.", args[1], 3000);
            Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.HISTORY, args[1]){

                @Override
                public void onSuccess() {
                    Globals.mc.addScheduledTask(() -> {
                        boolean first = true;
                        ChatUtil.sendMessage("");
                        for (Map.Entry entry : this.names.entrySet()) {
                            String dateString;
                            String string = dateString = ((Date)entry.getKey()).getTime() == 0L ? "" : new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format((Date)entry.getKey());
                            if (first) {
                                Managers.CHAT.sendDeleteMessage("\u00a7l" + (String)entry.getValue() + "\u00a77" + " - " + "\u00a76" + dateString, args[1], 3000);
                                first = false;
                                continue;
                            }
                            ChatUtil.sendMessage((String)entry.getValue() + "\u00a77" + " - " + "\u00a76" + dateString);
                        }
                        ChatUtil.sendMessage("");
                    });
                }

                @Override
                public void onFailure() {
                    Managers.CHAT.sendDeleteMessage("\u00a7cFailed to lookup \u00a7f" + args[1], args[1], 3000);
                }
            });
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1) {
            return inputs;
        }
        if (args.length == 2) {
            String player = LookUpUtil.findNextPlayerName(args[1]);
            return inputs.setCompletion(player == null ? "" : TextUtil.substring(player, args[1].length())).setRest("");
        }
        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        if (completer.getArgs().length == 1) {
            if (completer.getArgs()[0].equalsIgnoreCase("history")) {
                completer.setMcComplete(true);
            } else {
                completer.setResult(Commands.getPrefix() + "history");
            }
        } else if (completer.getArgs().length == 2) {
            String player = LookUpUtil.findNextPlayerName(completer.getArgs()[1]);
            if (player == null || player.equalsIgnoreCase(completer.getArgs()[1])) {
                completer.setMcComplete(true);
            } else {
                completer.setResult(Commands.getPrefix() + "history " + player);
            }
        }
        return completer;
    }
}
