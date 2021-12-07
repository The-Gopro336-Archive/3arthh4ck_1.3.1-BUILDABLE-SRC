package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.modules.combat.surround.modes.Movement;

class Surround$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement = new int[Movement.values().length];
        try {
            Surround$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement[Movement.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Surround$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement[Movement.Static.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Surround$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement[Movement.Limit.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Surround$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$surround$modes$Movement[Movement.Disable.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
