package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.impl.util.minecraft.CooldownBypass;

class BlockPlacingModule$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass = new int[CooldownBypass.values().length];
        try {
            BlockPlacingModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPlacingModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Pick.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPlacingModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Slot.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
