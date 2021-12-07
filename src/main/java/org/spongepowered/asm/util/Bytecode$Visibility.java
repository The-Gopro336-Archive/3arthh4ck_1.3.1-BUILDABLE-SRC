package org.spongepowered.asm.util;

public enum Bytecode$Visibility {
    PRIVATE(2),
    PROTECTED(4),
    PACKAGE(0),
    PUBLIC(1);

    static final int MASK = 7;
    final int access;

    private Bytecode$Visibility(int access) {
        this.access = access;
    }
}
