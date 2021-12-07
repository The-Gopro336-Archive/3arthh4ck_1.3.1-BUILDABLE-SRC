package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.impl.util.helpers.blocks.modes.FastHelping;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;

class ObbyModule$2 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass;
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$FastHelping;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$FastHelping = new int[FastHelping.values().length];
        try {
            ObbyModule$2.$SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$FastHelping[FastHelping.Off.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ObbyModule$2.$SwitchMap$me$earth$earthhack$impl$util$helpers$blocks$modes$FastHelping[FastHelping.Down.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass = new int[CooldownBypass.values().length];
        try {
            ObbyModule$2.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ObbyModule$2.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Slot.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ObbyModule$2.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Pick.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
