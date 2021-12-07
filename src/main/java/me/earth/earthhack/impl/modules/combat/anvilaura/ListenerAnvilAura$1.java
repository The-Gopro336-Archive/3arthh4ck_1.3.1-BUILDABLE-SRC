package me.earth.earthhack.impl.modules.combat.anvilaura;

import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;

class ListenerAnvilAura$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode = new int[AnvilMode.values().length];
        try {
            ListenerAnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode[AnvilMode.Render.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerAnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode[AnvilMode.Pressure.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerAnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode[AnvilMode.Mine.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerAnvilAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$anvilaura$modes$AnvilMode[AnvilMode.High.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
