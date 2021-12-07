package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;

class ListenerCollision$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode = new int[PhaseMode.values().length];
        try {
            ListenerCollision$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Constantiam.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerCollision$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.ConstantiamNew.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerCollision$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Normal.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerCollision$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Sand.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerCollision$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$phase$mode$PhaseMode[PhaseMode.Climb.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
