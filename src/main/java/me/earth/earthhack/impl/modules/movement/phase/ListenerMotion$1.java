package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode = new int[PhaseMode.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Constantiam.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Normal.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Sand.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Packet.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Skip.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
