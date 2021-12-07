package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.modules.combat.offhand.modes.HUDMode;

class Offhand$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$offhand$modes$HUDMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$offhand$modes$HUDMode = new int[HUDMode.values().length];
        try {
            Offhand$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$offhand$modes$HUDMode[HUDMode.Info.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Offhand$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$offhand$modes$HUDMode[HUDMode.Name.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
