package org.spongepowered.asm.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.util.JavaVersion;

final class MixinEnvironment$CompatibilityLevel$2
extends MixinEnvironment.CompatibilityLevel {
    MixinEnvironment$CompatibilityLevel$2(int ver, int classVersion, boolean resolveMethodsInInterfaces) {
    }

    @Override
    boolean isSupported() {
        return JavaVersion.current() >= 1.8;
    }
}
