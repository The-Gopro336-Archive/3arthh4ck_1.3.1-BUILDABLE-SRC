package me.earth.earthhack.impl.managers.config.helpers;

import me.earth.earthhack.impl.managers.client.macro.MacroType;

class MacroConfigHelper$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType;

    static {
        $SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType = new int[MacroType.values().length];
        try {
            MacroConfigHelper$1.$SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType[MacroType.NORMAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MacroConfigHelper$1.$SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType[MacroType.FLOW.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MacroConfigHelper$1.$SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType[MacroType.COMBINED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MacroConfigHelper$1.$SwitchMap$me$earth$earthhack$impl$managers$client$macro$MacroType[MacroType.DELEGATE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
