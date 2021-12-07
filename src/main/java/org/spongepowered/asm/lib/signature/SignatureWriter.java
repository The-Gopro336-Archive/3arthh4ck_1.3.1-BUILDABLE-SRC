package org.spongepowered.asm.lib.signature;

import org.spongepowered.asm.lib.signature.SignatureVisitor;

public class SignatureWriter
extends SignatureVisitor {
    private final StringBuilder buf = new StringBuilder();
    private boolean hasFormals;
    private boolean hasParameters;
    private int argumentStack;

    public SignatureWriter() {
        super(327680);
    }

    public void visitFormalTypeParameter(String name) {
        if (!this.hasFormals) {
            this.hasFormals = true;
            this.buf.append('<');
        }
        this.buf.append(name);
        this.buf.append(':');
    }

    public SignatureVisitor visitClassBound() {
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.buf.append(':');
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        this.endFormals();
        return this;
    }

    public SignatureVisitor visitInterface() {
        return this;
    }

    public SignatureVisitor visitParameterType() {
        this.endFormals();
        if (!this.hasParameters) {
            this.hasParameters = true;
            this.buf.append('(');
        }
        return this;
    }

    public SignatureVisitor visitReturnType() {
        this.endFormals();
        if (!this.hasParameters) {
            this.buf.append('(');
        }
        this.buf.append(')');
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        this.buf.append('^');
        return this;
    }

    public void visitBaseType(char descriptor) {
        this.buf.append(descriptor);
    }

    public void visitTypeVariable(String name) {
        this.buf.append('T');
        this.buf.append(name);
        this.buf.append(';');
    }

    public SignatureVisitor visitArrayType() {
        this.buf.append('[');
        return this;
    }

    public void visitClassType(String name) {
        this.buf.append('L');
        this.buf.append(name);
        this.argumentStack *= 2;
    }

    public void visitInnerClassType(String name) {
        this.endArguments();
        this.buf.append('.');
        this.buf.append(name);
        this.argumentStack *= 2;
    }

    public void visitTypeArgument() {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.buf.append('<');
        }
        this.buf.append('*');
    }

    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.buf.append('<');
        }
        if (wildcard != '=') {
            this.buf.append(wildcard);
        }
        return this;
    }

    public void visitEnd() {
        this.endArguments();
        this.buf.append(';');
    }

    public String toString() {
        return this.buf.toString();
    }

    private void endFormals() {
        if (this.hasFormals) {
            this.hasFormals = false;
            this.buf.append('>');
        }
    }

    private void endArguments() {
        if (this.argumentStack % 2 != 0) {
            this.buf.append('>');
        }
        this.argumentStack /= 2;
    }
}
