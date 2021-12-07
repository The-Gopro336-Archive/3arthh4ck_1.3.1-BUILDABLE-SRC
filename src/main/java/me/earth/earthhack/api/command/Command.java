/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.command;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class Command
implements Nameable {
    private final String name;
    private final String fullUsage;
    private final String[][] usage;
    private final boolean hidden;

    public Command(String[][] usage) {
        this(usage, false);
    }

    public Command(String[][] usage, boolean hidden) {
        if (usage == null || usage.length == 0 || usage[0].length != 1) {
            throw new IllegalArgumentException("Usage of command needs to be an 2 dimensional array with a length > 0 and the first entry needs to have a length of 1.");
        }
        this.name = usage[0][0];
        this.usage = usage;
        this.hidden = hidden;
        this.fullUsage = this.concatenateUsage(0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean fits(String[] args) {
        return args[0].length() > 0 && TextUtil.startsWith(this.name, args[0]);
    }

    public abstract void execute(String[] var1);

    public PossibleInputs getPossibleInputs(String[] args) {
        if (args == null || args.length == 0) {
            return PossibleInputs.empty();
        }
        if (args.length == 1) {
            String completion = TextUtil.substring(this.name, args[0].length());
            String rest = TextUtil.substring(this.getFullUsage(), this.name.length());
            return new PossibleInputs(completion, rest);
        }
        if (args.length <= this.usage.length) {
            String last = this.getLast(args);
            String completion = TextUtil.substring(last, args[args.length - 1].length());
            return new PossibleInputs(completion, this.concatenateUsage(args.length));
        }
        return PossibleInputs.empty();
    }

    public Completer onTabComplete(Completer completer) {
        if (completer.isSame() && completer.getArgs().length <= this.usage.length) {
            int i;
            String[] args = this.usage[completer.getArgs().length - 1];
            for (i = 0; i < args.length && !args[i].equalsIgnoreCase(completer.getArgs()[completer.getArgs().length - 1]); ++i) {
            }
            String arg = i >= args.length - 1 ? args[0] : args[i + 1];
            String newInitial = completer.getInitial().trim().substring(0, completer.getInitial().trim().length() - completer.getArgs()[completer.getArgs().length - 1].length());
            completer.setResult(newInitial + arg);
            return completer;
        }
        PossibleInputs inputs = this.getPossibleInputs(completer.getArgs());
        if (!inputs.getCompletion().isEmpty()) {
            completer.setResult(completer.getInitial().trim() + inputs.getCompletion());
            return completer;
        }
        completer.setMcComplete(true);
        return completer;
    }

    public String[][] getUsage() {
        return this.usage;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public String getFullUsage() {
        return this.fullUsage;
    }

    private String getLast(String[] args) {
        if (args.length <= this.usage.length) {
            String[] array;
            String last = args[args.length - 1];
            for (String string : array = this.usage[args.length - 1]) {
                if (!TextUtil.startsWith(string, last)) continue;
                return string;
            }
        }
        return "";
    }

    private String concatenateUsage(int index) {
        int j;
        if (this.usage.length == 1) {
            return this.name;
        }
        if (index >= this.usage.length) {
            return "";
        }
        StringBuilder builder = new StringBuilder(index == 0 ? this.name : "");
        int n = j = index == 0 ? 1 : index;
        while (j < this.usage.length) {
            builder.append(" <");
            for (int i = 0; i < this.usage[j].length; ++i) {
                builder.append(this.usage[j][i]).append("/");
            }
            builder.replace(builder.length() - 1, builder.length(), ">");
            ++j;
        }
        return builder.toString();
    }
}

