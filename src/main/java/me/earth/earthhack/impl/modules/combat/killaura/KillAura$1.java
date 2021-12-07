package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTeleport;

class KillAura$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$killaura$util$AuraTeleport;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$killaura$util$AuraTeleport = new int[AuraTeleport.values().length];
        try {
            KillAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$killaura$util$AuraTeleport[AuraTeleport.Smart.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            KillAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$killaura$util$AuraTeleport[AuraTeleport.Full.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
