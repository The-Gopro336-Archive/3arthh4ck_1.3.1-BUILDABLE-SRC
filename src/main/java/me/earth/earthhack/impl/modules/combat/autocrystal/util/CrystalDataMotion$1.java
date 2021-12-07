package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalDataMotion;

class CrystalDataMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing = new int[CrystalDataMotion.Timing.values().length];
        try {
            CrystalDataMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing[CrystalDataMotion.Timing.BOTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrystalDataMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing[CrystalDataMotion.Timing.PRE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrystalDataMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing[CrystalDataMotion.Timing.POST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrystalDataMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$util$CrystalDataMotion$Timing[CrystalDataMotion.Timing.NONE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
