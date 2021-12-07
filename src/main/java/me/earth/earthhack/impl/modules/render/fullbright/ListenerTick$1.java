package me.earth.earthhack.impl.modules.render.fullbright;

import me.earth.earthhack.impl.modules.render.fullbright.mode.BrightMode;

class ListenerTick$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$render$fullbright$mode$BrightMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$render$fullbright$mode$BrightMode = new int[BrightMode.values().length];
        try {
            ListenerTick$1.$SwitchMap$me$earth$earthhack$impl$modules$render$fullbright$mode$BrightMode[BrightMode.Gamma.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerTick$1.$SwitchMap$me$earth$earthhack$impl$modules$render$fullbright$mode$BrightMode[BrightMode.Potion.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
