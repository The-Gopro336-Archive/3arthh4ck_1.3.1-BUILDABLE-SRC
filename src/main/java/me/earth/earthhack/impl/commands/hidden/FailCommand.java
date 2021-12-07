package me.earth.earthhack.impl.commands.hidden;

import java.util.Arrays;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class FailCommand
extends Command
implements Registrable,
Globals {
    private final StopWatch indexTimer = new StopWatch();
    private int index;

    public FailCommand() {
        super(new String[][]{{"fail"}}, true);
    }

    @Override
    public void execute(String[] args) {
        if (args != null && args.length != 0) {
            Command closest = null;
            int closestDistance = Integer.MAX_VALUE;
            for (Command command : Managers.COMMANDS.getRegistered()) {
                int levenshtein = CommandUtil.levenshtein(command.getName(), args[0]);
                if (levenshtein >= closestDistance) continue;
                closest = command;
                closestDistance = levenshtein;
            }
            if (closest != null) {
                ChatUtil.sendMessage("\u00a7cCommand not found, did you mean " + closest.getName() + "?. Type " + Commands.getPrefix() + "help to get a list of commands.");
                Earthhack.getLogger().info("FailCommand for args: " + Arrays.toString(args));
                return;
            }
        }
        ChatUtil.sendMessage("\u00a7cCommand not found. Type " + Commands.getPrefix() + "help to get a list of commands.");
        Earthhack.getLogger().info("FailCommand for args: " + Arrays.toString(args));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        String conc = Managers.COMMANDS.getConcatenatedCommands();
        if (conc == null || conc.isEmpty()) {
            return PossibleInputs.empty().setRest("\u00a7cerror");
        }
        if (this.indexTimer.passed(750L)) {
            this.index += 10;
            this.indexTimer.reset();
        }
        if (this.index >= conc.length()) {
            this.index = 0;
        }
        return PossibleInputs.empty().setRest("\u00a7c" + conc.substring(this.index) + ", " + conc);
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        completer.setMcComplete(true);
        return completer;
    }
}
