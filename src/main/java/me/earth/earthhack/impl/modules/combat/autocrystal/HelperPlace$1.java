package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.modules.combat.autocrystal.modes.PreCalc;

class HelperPlace$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$PreCalc;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$PreCalc = new int[PreCalc.values().length];
        try {
            HelperPlace$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$PreCalc[PreCalc.Damage.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HelperPlace$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$PreCalc[PreCalc.Target.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
