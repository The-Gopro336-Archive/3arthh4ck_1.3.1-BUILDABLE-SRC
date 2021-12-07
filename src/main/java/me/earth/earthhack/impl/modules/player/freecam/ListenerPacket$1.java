package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.modules.player.freecam.mode.CamMode;

class ListenerPacket$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$player$freecam$mode$CamMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$player$freecam$mode$CamMode = new int[CamMode.values().length];
        try {
            ListenerPacket$1.$SwitchMap$me$earth$earthhack$impl$modules$player$freecam$mode$CamMode[CamMode.Cancel.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerPacket$1.$SwitchMap$me$earth$earthhack$impl$modules$player$freecam$mode$CamMode[CamMode.Spanish.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerPacket$1.$SwitchMap$me$earth$earthhack$impl$modules$player$freecam$mode$CamMode[CamMode.Position.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
