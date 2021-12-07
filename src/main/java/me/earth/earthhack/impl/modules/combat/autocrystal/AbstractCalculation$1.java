package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;

class AbstractCalculation$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity = new int[BreakValidity.values().length];
        try {
            AbstractCalculation$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.VALID.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractCalculation$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.ROTATIONS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractCalculation$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.INVALID.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
