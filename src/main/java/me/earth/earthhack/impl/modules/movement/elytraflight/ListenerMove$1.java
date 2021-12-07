package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;

class ListenerMove$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode = new int[ElytraMode.values().length];
        try {
            ListenerMove$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode[ElytraMode.Wasp.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMove$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode[ElytraMode.Packet.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMove$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode[ElytraMode.Boost.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMove$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode[ElytraMode.Control.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMove$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$elytraflight$mode$ElytraMode[ElytraMode.Normal.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
