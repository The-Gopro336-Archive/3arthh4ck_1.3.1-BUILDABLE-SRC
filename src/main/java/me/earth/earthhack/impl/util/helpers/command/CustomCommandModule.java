/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.helpers.command;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public interface CustomCommandModule {
    public static final String[] DEFAULT_ARGS = new String[0];

    default public boolean execute(String[] args) {
        return false;
    }

    default public boolean getInput(String[] args, PossibleInputs inputs) {
        return false;
    }

    default public CustomCompleterResult complete(Completer completer) {
        return CustomCompleterResult.PASS;
    }

    default public String[] getArgs() {
        return DEFAULT_ARGS;
    }
}

