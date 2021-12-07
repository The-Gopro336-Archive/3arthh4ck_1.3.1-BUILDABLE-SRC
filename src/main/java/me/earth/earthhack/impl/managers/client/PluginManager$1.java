package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.vanilla.Environment;

class PluginManager$1 {
    static final int[] $SwitchMap$me$earth$earthhack$vanilla$Environment;

    static {
        $SwitchMap$me$earth$earthhack$vanilla$Environment = new int[Environment.values().length];
        try {
            PluginManager$1.$SwitchMap$me$earth$earthhack$vanilla$Environment[Environment.VANILLA.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PluginManager$1.$SwitchMap$me$earth$earthhack$vanilla$Environment[Environment.SEARGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PluginManager$1.$SwitchMap$me$earth$earthhack$vanilla$Environment[Environment.MCP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
