package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$antitrap$util$AntiTrapMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$antitrap$util$AntiTrapMode = new int[AntiTrapMode.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$antitrap$util$AntiTrapMode[AntiTrapMode.Crystal.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$antitrap$util$AntiTrapMode[AntiTrapMode.FacePlace.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$antitrap$util$AntiTrapMode[AntiTrapMode.Fill.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
