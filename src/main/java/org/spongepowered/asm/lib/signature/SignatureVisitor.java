package org.spongepowered.asm.lib.signature;

public abstract class SignatureVisitor {
    public static final char EXTENDS = '+';
    public static final char SUPER = '-';
    public static final char INSTANCEOF = '=';
    protected final int api;

    public SignatureVisitor(int api) {
        if (api != 262144 && api != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = api;
    }

    public void visitFormalTypeParameter(String name) {
    }

    public SignatureVisitor visitClassBound() {
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        return this;
    }

    public SignatureVisitor visitInterface() {
        return this;
    }

    public SignatureVisitor visitParameterType() {
        return this;
    }

    public SignatureVisitor visitReturnType() {
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        return this;
    }

    public void visitBaseType(char descriptor) {
    }

    public void visitTypeVariable(String name) {
    }

    public SignatureVisitor visitArrayType() {
        return this;
    }

    public void visitClassType(String name) {
    }

    public void visitInnerClassType(String name) {
    }

    public void visitTypeArgument() {
    }

    public SignatureVisitor visitTypeArgument(char wildcard) {
        return this;
    }

    public void visitEnd() {
    }
}
