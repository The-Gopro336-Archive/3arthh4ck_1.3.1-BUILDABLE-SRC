package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.util.bind.Toggle;

class KeyBoardManager$3 {
    static final int[] $SwitchMap$me$earth$earthhack$api$util$bind$Toggle;

    static {
        $SwitchMap$me$earth$earthhack$api$util$bind$Toggle = new int[Toggle.values().length];
        try {
            KeyBoardManager$3.$SwitchMap$me$earth$earthhack$api$util$bind$Toggle[Toggle.Hold.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            KeyBoardManager$3.$SwitchMap$me$earth$earthhack$api$util$bind$Toggle[Toggle.Disable.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
