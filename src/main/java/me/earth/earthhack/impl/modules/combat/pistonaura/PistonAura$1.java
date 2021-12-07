package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.modules.combat.pistonaura.modes.PistonTarget;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;

class PistonAura$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$modes$PistonTarget;
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage = new int[PistonStage.values().length];
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage[PistonStage.CRYSTAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage[PistonStage.PISTON.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage[PistonStage.REDSTONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$util$PistonStage[PistonStage.BREAK.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$modes$PistonTarget = new int[PistonTarget.values().length];
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$modes$PistonTarget[PistonTarget.FOV.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$modes$PistonTarget[PistonTarget.Closest.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$pistonaura$modes$PistonTarget[PistonTarget.Calc.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
