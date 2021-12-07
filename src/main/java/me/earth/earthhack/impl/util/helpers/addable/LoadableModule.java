/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.addable.AddableModule;

public abstract class LoadableModule
extends AddableModule {
    public LoadableModule(String name, Category category, String command, String descriptor) {
        super(name, category, command, descriptor);
    }

    protected abstract void load(String var1, boolean var2);

    protected abstract String getLoadableStartingWith(String var1);

    @Override
    public boolean execute(String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("load")) {
            if (args.length == 2) {
                this.load(null, true);
            } else {
                this.load(args[2], false);
            }
            return true;
        }
        return super.execute(args);
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs) {
        if (args == null) {
            return false;
        }
        if (args.length == 1) {
            String name = this.getName();
            inputs.setCompletion(TextUtil.substring(name, args[0].length())).setRest(" <add/del/load/setting> <value>");
            return true;
        }
        if (args.length > 1 && "load".startsWith(args[1].toLowerCase())) {
            if (args.length == 2) {
                inputs.setCompletion(TextUtil.substring("load", args[1].length())).setRest(" <" + this.descriptor + ">");
            } else {
                String s = this.getLoadableStartingWith(args[2]);
                if (s == null) {
                    inputs.setCompletion("").setRest("");
                } else {
                    inputs.setCompletion(TextUtil.substring(s, args[2].length())).setRest("");
                }
            }
            return true;
        }
        return super.getInput(args, inputs);
    }
}

