/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;

public abstract class AbstractModuleCommand
extends Command {
    protected final int index;

    public AbstractModuleCommand(String name, String[][] additionalArgs) {
        this(AbstractModuleCommand.concatArray(name, additionalArgs), 1);
    }

    public AbstractModuleCommand(String[][] usage, int index) {
        super(usage);
        if (index < 0) {
            throw new IllegalArgumentException("Index is smaller than 0!");
        }
        this.index = index;
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > this.index) {
            Module module = this.getModule(args, this.index);
            if (module == null) {
                inputs.setCompletion("").setRest("\u00a7c not found.");
            } else if (args.length == this.index + 1) {
                inputs.setCompletion(TextUtil.substring(module.getName(), args[this.index].length()));
            }
        }
        return inputs;
    }

    protected Module getModule(String[] args, int index) {
        if (args.length <= index) {
            return null;
        }
        return (Module)CommandUtil.getNameableStartingWith(args[index], Managers.MODULES.getRegistered());
    }

    private static String[][] concatArray(String name, String[][] args) {
        String[][] concat = new String[args.length + 2][];
        concat[0] = new String[]{name};
        concat[1] = new String[]{"module"};
        System.arraycopy(args, 0, concat, 2, args.length);
        return concat;
    }
}

