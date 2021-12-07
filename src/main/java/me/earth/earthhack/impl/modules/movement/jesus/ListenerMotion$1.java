package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$jesus$mode$JesusMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$jesus$mode$JesusMode = new int[JesusMode.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$jesus$mode$JesusMode[JesusMode.Dolphin.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$jesus$mode$JesusMode[JesusMode.Trampoline.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
