package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;

class Timer$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode = new int[TimerMode.values().length];
        try {
            Timer$1.$SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode[TimerMode.Switch.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Timer$1.$SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode[TimerMode.Physics.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Timer$1.$SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode[TimerMode.Blink.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Timer$1.$SwitchMap$me$earth$earthhack$impl$modules$player$timer$mode$TimerMode[TimerMode.Normal.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
