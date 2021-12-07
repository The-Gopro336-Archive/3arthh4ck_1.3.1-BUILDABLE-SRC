package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$Rotate;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$Rotate = new int[Rotate.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$Rotate[Rotate.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$Rotate[Rotate.Normal.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$Rotate[Rotate.Packet.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
