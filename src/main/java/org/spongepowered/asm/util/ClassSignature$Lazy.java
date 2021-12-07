package org.spongepowered.asm.util;

import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$Lazy
extends ClassSignature {
    private final String sig;
    private ClassSignature generated;

    ClassSignature$Lazy(String sig) {
        this.sig = sig;
    }

    @Override
    public ClassSignature wake() {
        if (this.generated == null) {
            this.generated = ClassSignature.of(this.sig);
        }
        return this.generated;
    }
}
