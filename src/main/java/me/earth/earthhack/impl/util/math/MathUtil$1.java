package me.earth.earthhack.impl.util.math;

import net.minecraft.util.EnumFacing;

class MathUtil$1 {
    static final int[] $SwitchMap$net$minecraft$util$EnumFacing;

    static {
        $SwitchMap$net$minecraft$util$EnumFacing = new int[EnumFacing.values().length];
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.DOWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.UP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.WEST.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MathUtil$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.EAST.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
