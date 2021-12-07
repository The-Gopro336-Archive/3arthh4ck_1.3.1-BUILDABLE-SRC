package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;

class ListenerDamage$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode = new int[MineMode.values().length];
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Reset.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Packet.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Civ.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Damage.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Smart.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerDamage$1.$SwitchMap$me$earth$earthhack$impl$modules$player$speedmine$mode$MineMode[MineMode.Instant.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
