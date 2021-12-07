package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.util.minecraft.CooldownBypass;

class HelperRotation$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass = new int[CooldownBypass.values().length];
        try {
            HelperRotation$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HelperRotation$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Slot.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HelperRotation$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Pick.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
