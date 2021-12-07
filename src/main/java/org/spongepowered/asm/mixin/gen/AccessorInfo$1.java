package org.spongepowered.asm.mixin.gen;

import org.spongepowered.asm.mixin.gen.AccessorInfo;

class AccessorInfo$1 {
    static final int[] $SwitchMap$org$spongepowered$asm$mixin$gen$AccessorInfo$AccessorType;

    static {
        $SwitchMap$org$spongepowered$asm$mixin$gen$AccessorInfo$AccessorType = new int[AccessorInfo.AccessorType.values().length];
        try {
            AccessorInfo$1.$SwitchMap$org$spongepowered$asm$mixin$gen$AccessorInfo$AccessorType[AccessorInfo.AccessorType.FIELD_GETTER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AccessorInfo$1.$SwitchMap$org$spongepowered$asm$mixin$gen$AccessorInfo$AccessorType[AccessorInfo.AccessorType.FIELD_SETTER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
