package org.spongepowered.asm.util;

import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureParser$BoundElement
extends ClassSignature.SignatureParser.TokenElement {
    private final ClassSignature.SignatureParser.TokenElement type;
    private final boolean classBound;

    ClassSignature$SignatureParser$BoundElement(ClassSignature.SignatureParser.TokenElement type, boolean classBound) {
        super(SignatureParser.this);
        this.type = type;
        this.classBound = classBound;
    }

    @Override
    public void visitClassType(String name) {
        this.token = this.type.token.addBound(name, this.classBound);
    }

    @Override
    public void visitTypeArgument() {
        this.token.addTypeArgument('*');
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        return new ClassSignature.SignatureParser.TypeArgElement(SignatureParser.this, this, wildcard);
    }
}
