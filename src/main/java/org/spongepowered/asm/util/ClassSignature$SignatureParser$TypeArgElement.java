package org.spongepowered.asm.util;

import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureParser$TypeArgElement
extends ClassSignature.SignatureParser.TokenElement {
    private final ClassSignature.SignatureParser.TokenElement type;
    private final char wildcard;

    ClassSignature$SignatureParser$TypeArgElement(ClassSignature.SignatureParser.TokenElement type, char wildcard) {
        super(SignatureParser.this);
        this.type = type;
        this.wildcard = wildcard;
    }

    @Override
    public SignatureVisitor visitArrayType() {
        this.type.setArray();
        return this;
    }

    @Override
    public void visitBaseType(char descriptor) {
        this.token = this.type.addTypeArgument(descriptor).asToken();
    }

    @Override
    public void visitTypeVariable(String name) {
        ClassSignature.TokenHandle token = SignatureParser.this.this$0.getType(name);
        this.token = this.type.addTypeArgument(token).setWildcard(this.wildcard).asToken();
    }

    @Override
    public void visitClassType(String name) {
        this.token = this.type.addTypeArgument(name).setWildcard(this.wildcard).asToken();
    }

    @Override
    public void visitTypeArgument() {
        this.token.addTypeArgument('*');
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        return new ClassSignature$SignatureParser$TypeArgElement(this, wildcard);
    }

    @Override
    public void visitEnd() {
    }
}
