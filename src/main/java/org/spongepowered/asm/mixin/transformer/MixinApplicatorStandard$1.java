package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.MixinApplicatorStandard;

class MixinApplicatorStandard$1 {
    static final int[] $SwitchMap$org$spongepowered$asm$mixin$transformer$MixinApplicatorStandard$ApplicatorPass;

    static {
        $SwitchMap$org$spongepowered$asm$mixin$transformer$MixinApplicatorStandard$ApplicatorPass = new int[MixinApplicatorStandard.ApplicatorPass.values().length];
        try {
            MixinApplicatorStandard$1.$SwitchMap$org$spongepowered$asm$mixin$transformer$MixinApplicatorStandard$ApplicatorPass[MixinApplicatorStandard.ApplicatorPass.MAIN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinApplicatorStandard$1.$SwitchMap$org$spongepowered$asm$mixin$transformer$MixinApplicatorStandard$ApplicatorPass[MixinApplicatorStandard.ApplicatorPass.PREINJECT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinApplicatorStandard$1.$SwitchMap$org$spongepowered$asm$mixin$transformer$MixinApplicatorStandard$ApplicatorPass[MixinApplicatorStandard.ApplicatorPass.INJECT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
