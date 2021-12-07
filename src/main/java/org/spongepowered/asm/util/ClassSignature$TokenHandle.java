package org.spongepowered.asm.util;

import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$TokenHandle
implements ClassSignature.IToken {
    final ClassSignature.Token token;
    boolean array;
    char wildcard;

    ClassSignature$TokenHandle() {
        this(new ClassSignature.Token());
    }

    ClassSignature$TokenHandle(ClassSignature.Token token) {
        this.token = token;
    }

    @Override
    public ClassSignature.IToken setArray(boolean array) {
        this.array |= array;
        return this;
    }

    @Override
    public ClassSignature.IToken setWildcard(char wildcard) {
        if ("+-".indexOf(wildcard) > -1) {
            this.wildcard = wildcard;
        }
        return this;
    }

    @Override
    public String asBound() {
        return this.token.asBound();
    }

    @Override
    public String asType() {
        StringBuilder sb = new StringBuilder();
        if (this.wildcard > '\u0000') {
            sb.append(this.wildcard);
        }
        if (this.array) {
            sb.append('[');
        }
        return sb.append(ClassSignature.this.getTypeVar(this)).toString();
    }

    @Override
    public ClassSignature.Token asToken() {
        return this.token;
    }

    public String toString() {
        return this.token.toString();
    }

    public ClassSignature$TokenHandle clone() {
        return new ClassSignature$TokenHandle(this.token);
    }
}
