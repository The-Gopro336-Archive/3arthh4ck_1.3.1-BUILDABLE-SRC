package me.earth.earthhack.impl.modules.combat.anvilaura;

import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilStage;

class AnvilAura$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilStage;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilStage = new int[AnvilStage.values().length];
        try {
            AnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilStage[AnvilStage.OBSIDIAN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilStage[AnvilStage.PRESSURE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilStage[AnvilStage.CRYSTAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
