package me.earth.earthhack.impl.util.animation;

import me.earth.earthhack.impl.util.animation.AnimationMode;

class TimeAnimation$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$animation$AnimationMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$animation$AnimationMode = new int[AnimationMode.values().length];
        try {
            TimeAnimation$1.$SwitchMap$me$earth$earthhack$impl$util$animation$AnimationMode[AnimationMode.LINEAR.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TimeAnimation$1.$SwitchMap$me$earth$earthhack$impl$util$animation$AnimationMode[AnimationMode.EXPONENTIAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
