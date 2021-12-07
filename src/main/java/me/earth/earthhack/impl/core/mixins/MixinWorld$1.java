package me.earth.earthhack.impl.core.mixins;

import net.minecraft.util.EnumParticleTypes;

class MixinWorld$1 {
    static final int[] $SwitchMap$net$minecraft$util$EnumParticleTypes;

    static {
        $SwitchMap$net$minecraft$util$EnumParticleTypes = new int[EnumParticleTypes.values().length];
        try {
            MixinWorld$1.$SwitchMap$net$minecraft$util$EnumParticleTypes[EnumParticleTypes.EXPLOSION_NORMAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinWorld$1.$SwitchMap$net$minecraft$util$EnumParticleTypes[EnumParticleTypes.EXPLOSION_LARGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinWorld$1.$SwitchMap$net$minecraft$util$EnumParticleTypes[EnumParticleTypes.EXPLOSION_HUGE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinWorld$1.$SwitchMap$net$minecraft$util$EnumParticleTypes[EnumParticleTypes.FIREWORKS_SPARK.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
