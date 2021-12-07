package me.earth.earthhack.impl.modules.combat.criticals;

import me.earth.earthhack.impl.modules.combat.criticals.mode.CritMode;

class ListenerUseEntity$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode = new int[CritMode.values().length];
        try {
            ListenerUseEntity$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode[CritMode.Packet.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerUseEntity$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode[CritMode.Bypass.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerUseEntity$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode[CritMode.Jump.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerUseEntity$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$criticals$mode$CritMode[CritMode.MiniJump.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
