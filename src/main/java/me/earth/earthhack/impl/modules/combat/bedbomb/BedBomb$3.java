package me.earth.earthhack.impl.modules.combat.bedbomb;

import me.earth.earthhack.impl.modules.combat.bedbomb.BedBomb;
import net.minecraft.util.EnumFacing;

class BedBomb$3 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$bedbomb$BedBomb$Logic;
    static final int[] $SwitchMap$net$minecraft$util$EnumFacing;

    static {
        $SwitchMap$net$minecraft$util$EnumFacing = new int[EnumFacing.values().length];
        try {
            BedBomb$3.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.DOWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedBomb$3.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.UP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedBomb$3.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedBomb$3.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedBomb$3.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.WEST.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$combat$bedbomb$BedBomb$Logic = new int[BedBomb.Logic.values().length];
        try {
            BedBomb$3.$SwitchMap$me$earth$earthhack$impl$modules$combat$bedbomb$BedBomb$Logic[BedBomb.Logic.BREAKPLACE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedBomb$3.$SwitchMap$me$earth$earthhack$impl$modules$combat$bedbomb$BedBomb$Logic[BedBomb.Logic.PLACEBREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
