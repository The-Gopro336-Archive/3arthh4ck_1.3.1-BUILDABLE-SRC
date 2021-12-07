package org.spongepowered.asm.util;

import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureParser$FormalParamElement
extends ClassSignature.SignatureParser.TokenElement {
    private final ClassSignature.TokenHandle handle;

    ClassSignature$SignatureParser$FormalParamElement(String param) {
        super(SignatureParser.this);
        this.handle = SignatureParser.this.this$0.getType(param);
        this.token = this.handle.asToken();
    }
}
