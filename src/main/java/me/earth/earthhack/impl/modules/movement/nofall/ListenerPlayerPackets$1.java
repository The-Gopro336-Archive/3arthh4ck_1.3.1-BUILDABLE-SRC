package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.impl.modules.movement.nofall.mode.FallMode;

class ListenerPlayerPackets$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$nofall$mode$FallMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$nofall$mode$FallMode = new int[FallMode.values().length];
        try {
            ListenerPlayerPackets$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$nofall$mode$FallMode[FallMode.Packet.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerPlayerPackets$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$nofall$mode$FallMode[FallMode.Anti.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerPlayerPackets$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$nofall$mode$FallMode[FallMode.AAC.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
