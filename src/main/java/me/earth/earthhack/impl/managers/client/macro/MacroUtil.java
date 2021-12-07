/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.client.macro;

import java.util.ArrayList;
import java.util.Arrays;
import me.earth.earthhack.impl.managers.client.macro.Macro;

public class MacroUtil {
    public static String[] concatenateCommands(Macro ... macros) {
        ArrayList<String> commands = new ArrayList<String>();
        for (Macro macro : macros) {
            commands.addAll(Arrays.asList(macro.getCommands()));
        }
        return commands.toArray(new String[0]);
    }
}

