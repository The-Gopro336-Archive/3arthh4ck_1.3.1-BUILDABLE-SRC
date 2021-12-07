package org.spongepowered.asm.util;

import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureParser
extends SignatureVisitor {
    private FormalParamElement param;

    ClassSignature$SignatureParser() {
        super(327680);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        this.param = new FormalParamElement(name);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return this.param.visitClassBound();
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return this.param.visitInterfaceBound();
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return new SuperClassElement();
    }

    @Override
    public SignatureVisitor visitInterface() {
        return new InterfaceElement();
    }

    class InterfaceElement
    extends TokenElement {
        InterfaceElement() {
        }

        @Override
        public void visitEnd() {
            ClassSignature.this.addInterface(this.token);
        }
    }

    class SuperClassElement
    extends TokenElement {
        SuperClassElement() {
        }

        @Override
        public void visitEnd() {
            ClassSignature.this.setSuperClass(this.token);
        }
    }

    class BoundElement
    extends TokenElement {
        private final TokenElement type;
        private final boolean classBound;

        BoundElement(TokenElement type, boolean classBound) {
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
            return new TypeArgElement(this, wildcard);
        }
    }

    class TypeArgElement
    extends TokenElement {
        private final TokenElement type;
        private final char wildcard;

        TypeArgElement(TokenElement type, char wildcard) {
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
            ClassSignature.TokenHandle token = ClassSignature.this.getType(name);
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
            return new TypeArgElement(this, wildcard);
        }

        @Override
        public void visitEnd() {
        }
    }

    class FormalParamElement
    extends TokenElement {
        private final ClassSignature.TokenHandle handle;

        FormalParamElement(String param) {
            this.handle = ClassSignature.this.getType(param);
            this.token = this.handle.asToken();
        }
    }

    abstract class TokenElement
    extends SignatureElement {
        protected ClassSignature.Token token;
        private boolean array;

        TokenElement() {
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
            return new BoundElement(this, true);
        }

        @Override
        public SignatureVisitor visitInterfaceBound() {
            this.getToken();
            return new BoundElement(this, false);
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
            return new TypeArgElement(this, wildcard);
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

    abstract class SignatureElement
    extends SignatureVisitor {
        public SignatureElement() {
            super(327680);
        }
    }
}
