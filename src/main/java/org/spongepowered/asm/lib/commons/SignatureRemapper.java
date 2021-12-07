package org.spongepowered.asm.lib.commons;

import java.util.Stack;
import org.spongepowered.asm.lib.commons.Remapper;
import org.spongepowered.asm.lib.signature.SignatureVisitor;

public class SignatureRemapper
extends SignatureVisitor {
    private final SignatureVisitor v;
    private final Remapper remapper;
    private Stack<String> classNames = new Stack();

    public SignatureRemapper(SignatureVisitor v, Remapper remapper) {
        this(327680, v, remapper);
    }

    protected SignatureRemapper(int api, SignatureVisitor v, Remapper remapper) {
        super(api);
        this.v = v;
        this.remapper = remapper;
    }

    public void visitClassType(String name) {
        this.classNames.push(name);
        this.v.visitClassType(this.remapper.mapType(name));
    }

    public void visitInnerClassType(String name) {
        String outerClassName = this.classNames.pop();
        String className = outerClassName + '$' + name;
        this.classNames.push(className);
        String remappedOuter = this.remapper.mapType(outerClassName) + '$';
        String remappedName = this.remapper.mapType(className);
        int index = remappedName.startsWith(remappedOuter) ? remappedOuter.length() : remappedName.lastIndexOf(36) + 1;
        this.v.visitInnerClassType(remappedName.substring(index));
    }

    public void visitFormalTypeParameter(String name) {
        this.v.visitFormalTypeParameter(name);
    }

    public void visitTypeVariable(String name) {
        this.v.visitTypeVariable(name);
    }

    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return this;
    }

    public void visitBaseType(char descriptor) {
        this.v.visitBaseType(descriptor);
    }

    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return this;
    }

    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return this;
    }

    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return this;
    }

    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return this;
    }

    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }

    public SignatureVisitor visitTypeArgument(char wildcard) {
        this.v.visitTypeArgument(wildcard);
        return this;
    }

    public void visitEnd() {
        this.v.visitEnd();
        this.classNames.pop();
    }
}
