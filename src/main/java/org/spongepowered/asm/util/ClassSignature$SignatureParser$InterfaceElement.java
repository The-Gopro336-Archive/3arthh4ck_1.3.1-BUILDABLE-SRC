package org.spongepowered.asm.util;

import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureParser$InterfaceElement
extends ClassSignature.SignatureParser.TokenElement {
    ClassSignature$SignatureParser$InterfaceElement() {
        super(SignatureParser.this);
    }

    @Override
    public void visitEnd() {
        SignatureParser.this.this$0.addInterface(this.token);
    }
}
