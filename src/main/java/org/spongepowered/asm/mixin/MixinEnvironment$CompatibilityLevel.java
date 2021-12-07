package org.spongepowered.asm.mixin;

import org.spongepowered.asm.util.JavaVersion;

public enum MixinEnvironment$CompatibilityLevel {
    JAVA_6(6, 50, false),
    JAVA_7(7, 51, false){

        @Override
        boolean isSupported() {
            return JavaVersion.current() >= 1.7;
        }
    }
    ,
    JAVA_8(8, 52, true){

        @Override
        boolean isSupported() {
            return JavaVersion.current() >= 1.8;
        }
    }
    ,
    JAVA_9(9, 53, true){

        @Override
        boolean isSupported() {
            return false;
        }
    };

    private static final int CLASS_V1_9 = 53;
    private final int ver;
    private final int classVersion;
    private final boolean supportsMethodsInInterfaces;
    private MixinEnvironment$CompatibilityLevel maxCompatibleLevel;

    private MixinEnvironment$CompatibilityLevel(int ver, int classVersion, boolean resolveMethodsInInterfaces) {
        this.ver = ver;
        this.classVersion = classVersion;
        this.supportsMethodsInInterfaces = resolveMethodsInInterfaces;
    }

    private void setMaxCompatibleLevel(MixinEnvironment$CompatibilityLevel maxCompatibleLevel) {
        this.maxCompatibleLevel = maxCompatibleLevel;
    }

    boolean isSupported() {
        return true;
    }

    public int classVersion() {
        return this.classVersion;
    }

    public boolean supportsMethodsInInterfaces() {
        return this.supportsMethodsInInterfaces;
    }

    public boolean isAtLeast(MixinEnvironment$CompatibilityLevel level) {
        return level == null || this.ver >= level.ver;
    }

    public boolean canElevateTo(MixinEnvironment$CompatibilityLevel level) {
        if (level == null || this.maxCompatibleLevel == null) {
            return true;
        }
        return level.ver <= this.maxCompatibleLevel.ver;
    }

    public boolean canSupport(MixinEnvironment$CompatibilityLevel level) {
        if (level == null) {
            return true;
        }
        return level.canElevateTo(this);
    }
}
