package me.earth.earthhack.impl.util.helpers.command;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.text.ChatUtil;

public abstract class AddableCommandModule
extends Module
implements CustomCommandModule {
    private static final String[] ARGS = new String[]{"add", "del"};

    public AddableCommandModule(String name, Category category) {
        super(name, category);
    }

    public abstract void add(String var1);

    public abstract void del(String var1);

    public abstract PossibleInputs getSettingInput(String var1, String[] var2);

    public void add(String[] args) {
        this.add(CommandUtil.concatenate(args, 2));
    }

    public void del(String[] args) {
        this.del(CommandUtil.concatenate(args, 2));
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length <= 1) {
            return false;
        }
        boolean add = args[1].equalsIgnoreCase("add");
        if (add || args[1].equalsIgnoreCase("del")) {
            if (args.length == 2) {
                ChatUtil.sendMessage("\u00a7cPlease specify what to add/delete!");
                return true;
            }
            if (add) {
                this.add(args);
            } else {
                this.del(args);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs) {
        if (args == null || args.length < 1) {
            return false;
        }
        if (args.length == 1) {
            String name = this.getName();
            inputs.setCompletion(TextUtil.substring(name, args[0].length())).setRest(" <add/del/setting> <value>");
            return true;
        }
        if (!args[0].equalsIgnoreCase(this.getName())) {
            return false;
        }
        String lower = args[1].toLowerCase();
        if ("add".startsWith(lower) || "del".startsWith(lower)) {
            String conc = args.length == 2 ? args[1] : CommandUtil.concatenate(args, 1);
            String[] sub = new String[args.length - 1];
            System.arraycopy(args, 1, sub, 0, args.length - 1);
            PossibleInputs si = this.getSettingInput(conc, sub);
            inputs.setCompletion(si.getCompletion()).setRest(si.getRest());
            return true;
        }
        return false;
    }

    @Override
    public CustomCompleterResult complete(Completer completer) {
        if (!completer.isSame() && completer.getArgs().length == 2) {
            return CustomCompleterResult.SUPER;
        }
        return CustomCompleterResult.PASS;
    }

    @Override
    public String[] getArgs() {
        return ARGS;
    }
}
