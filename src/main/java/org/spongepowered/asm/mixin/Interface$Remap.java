package org.spongepowered.asm.mixin;

public enum Interface$Remap {
    ALL,
    FORCE(true),
    ONLY_PREFIXED,
    NONE;

    private final boolean forceRemap;

    private Interface$Remap() {
        this(false);
    }

    private Interface$Remap(boolean forceRemap) {
        this.forceRemap = forceRemap;
    }

    public boolean forceRemap() {
        return this.forceRemap;
    }
}
