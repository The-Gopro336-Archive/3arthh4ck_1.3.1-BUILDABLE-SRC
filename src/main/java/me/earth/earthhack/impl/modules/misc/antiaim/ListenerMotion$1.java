package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.impl.modules.misc.antiaim.AntiAimMode;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode = new int[AntiAimMode.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Random.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Spin.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Down.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Headbang.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Horizontal.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$antiaim$AntiAimMode[AntiAimMode.Constant.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
