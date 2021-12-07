package org.spongepowered.asm.util;

import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.util.ClassSignature;

abstract class ClassSignature$SignatureParser$TokenElement
extends ClassSignature.SignatureParser.SignatureElement {
    protected ClassSignature.Token token;
    private boolean array;

    ClassSignature$SignatureParser$TokenElement() {
        super(SignatureParser.this);
    }

    public ClassSignature.Token getToken() {
        if (this.token == null) {
            this.token = new ClassSignature.Token();
        }
        return this.token;
    }

    protected void setArray() {
        this.array = true;
    }

    private boolean getArray() {
        boolean array = this.array;
        this.array = false;
        return array;
    }

    @Override
    public void visitClassType(String name) {
        this.getToken().setType(name);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        this.getToken();
        return new ClassSignature.SignatureParser.BoundElement(SignatureParser.this, this, true);
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        this.getToken();
        return new ClassSignature.SignatureParser.BoundElement(SignatureParser.this, this, false);
    }

    @Override
    public void visitInnerClassType(String name) {
        this.token.addInnerClass(name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
        this.setArray();
        return this;
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        return new ClassSignature.SignatureParser.TypeArgElement(SignatureParser.this, this, wildcard);
    }

    ClassSignature.Token addTypeArgument() {
        return this.token.addTypeArgument('*').asToken();
    }

    ClassSignature.IToken addTypeArgument(char symbol) {
        return this.token.addTypeArgument(symbol).setArray(this.getArray());
    }

    ClassSignature.IToken addTypeArgument(String name) {
        return this.token.addTypeArgument(name).setArray(this.getArray());
    }

    ClassSignature.IToken addTypeArgument(ClassSignature.Token token) {
        return this.token.addTypeArgument(token).setArray(this.getArray());
    }

    ClassSignature.IToken addTypeArgument(ClassSignature.TokenHandle token) {
        return this.token.addTypeArgument(token).setArray(this.getArray());
    }
}
