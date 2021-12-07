package org.spongepowered.asm.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.util.JavaVersion;

final class MixinEnvironment$CompatibilityLevel$1
extends MixinEnvironment.CompatibilityLevel {
    MixinEnvironment$CompatibilityLevel$1(int ver, int classVersion, boolean resolveMethodsInInterfaces) {
    }

    @Override
    boolean isSupported() {
        return JavaVersion.current() >= 1.7;
    }
}
