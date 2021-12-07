package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;

class ListenerSpawnObject$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity;
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass = new int[CooldownBypass.values().length];
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Slot.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$CooldownBypass[CooldownBypass.Pick.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity = new int[BreakValidity.values().length];
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.INVALID.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.ROTATIONS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerSpawnObject$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autocrystal$modes$BreakValidity[BreakValidity.VALID.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
