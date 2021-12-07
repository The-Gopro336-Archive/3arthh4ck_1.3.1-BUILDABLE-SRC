package org.spongepowered.asm.mixin.injection;

public enum InjectionPoint$Selector {
    FIRST,
    LAST,
    ONE;

    public static final InjectionPoint$Selector DEFAULT;

    static {
        DEFAULT = FIRST;
    }
}
