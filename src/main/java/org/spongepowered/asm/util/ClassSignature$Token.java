package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$Token
implements ClassSignature.IToken {
    static final String SYMBOLS = "+-*";
    private final boolean inner;
    private boolean array;
    private char symbol = '\u0000';
    private String type;
    private List<ClassSignature$Token> classBound;
    private List<ClassSignature$Token> ifaceBound;
    private List<ClassSignature.IToken> signature;
    private List<ClassSignature.IToken> suffix;
    private ClassSignature$Token tail;

    ClassSignature$Token() {
        this(false);
    }

    ClassSignature$Token(String type) {
        this(type, false);
    }

    ClassSignature$Token(char symbol) {
        this();
        this.symbol = symbol;
    }

    ClassSignature$Token(boolean inner) {
        this(null, inner);
    }

    ClassSignature$Token(String type, boolean inner) {
        this.inner = inner;
        this.type = type;
    }

    ClassSignature$Token setSymbol(char symbol) {
        if (this.symbol == '\u0000' && SYMBOLS.indexOf(symbol) > -1) {
            this.symbol = symbol;
        }
        return this;
    }

    ClassSignature$Token setType(String type) {
        if (this.type == null) {
            this.type = type;
        }
        return this;
    }

    boolean hasClassBound() {
        return this.classBound != null;
    }

    boolean hasInterfaceBound() {
        return this.ifaceBound != null;
    }

    @Override
    public ClassSignature.IToken setArray(boolean array) {
        this.array |= array;
        return this;
    }

    @Override
    public ClassSignature.IToken setWildcard(char wildcard) {
        if ("+-".indexOf(wildcard) == -1) {
            return this;
        }
        return this.setSymbol(wildcard);
    }

    private List<ClassSignature$Token> getClassBound() {
        if (this.classBound == null) {
            this.classBound = new ArrayList<ClassSignature$Token>();
        }
        return this.classBound;
    }

    private List<ClassSignature$Token> getIfaceBound() {
        if (this.ifaceBound == null) {
            this.ifaceBound = new ArrayList<ClassSignature$Token>();
        }
        return this.ifaceBound;
    }

    private List<ClassSignature.IToken> getSignature() {
        if (this.signature == null) {
            this.signature = new ArrayList<ClassSignature.IToken>();
        }
        return this.signature;
    }

    private List<ClassSignature.IToken> getSuffix() {
        if (this.suffix == null) {
            this.suffix = new ArrayList<ClassSignature.IToken>();
        }
        return this.suffix;
    }

    ClassSignature.IToken addTypeArgument(char symbol) {
        if (this.tail != null) {
            return this.tail.addTypeArgument(symbol);
        }
        ClassSignature$Token token = new ClassSignature$Token(symbol);
        this.getSignature().add(token);
        return token;
    }

    ClassSignature.IToken addTypeArgument(String name) {
        if (this.tail != null) {
            return this.tail.addTypeArgument(name);
        }
        ClassSignature$Token token = new ClassSignature$Token(name);
        this.getSignature().add(token);
        return token;
    }

    ClassSignature.IToken addTypeArgument(ClassSignature$Token token) {
        if (this.tail != null) {
            return this.tail.addTypeArgument(token);
        }
        this.getSignature().add(token);
        return token;
    }

    ClassSignature.IToken addTypeArgument(ClassSignature$TokenHandle token) {
        if (this.tail != null) {
            return this.tail.addTypeArgument(token);
        }
        ClassSignature$TokenHandle handle = token.clone();
        this.getSignature().add(handle);
        return handle;
    }

    ClassSignature$Token addBound(String bound, boolean classBound) {
        if (classBound) {
            return this.addClassBound(bound);
        }
        return this.addInterfaceBound(bound);
    }

    ClassSignature$Token addClassBound(String bound) {
        ClassSignature$Token token = new ClassSignature$Token(bound);
        this.getClassBound().add(token);
        return token;
    }

    ClassSignature$Token addInterfaceBound(String bound) {
        ClassSignature$Token token = new ClassSignature$Token(bound);
        this.getIfaceBound().add(token);
        return token;
    }

    ClassSignature$Token addInnerClass(String name) {
        this.tail = new ClassSignature$Token(name, true);
        this.getSuffix().add(this.tail);
        return this.tail;
    }

    public String toString() {
        return this.asType();
    }

    @Override
    public String asBound() {
        StringBuilder sb = new StringBuilder();
        if (this.type != null) {
            sb.append(this.type);
        }
        if (this.classBound != null) {
            for (ClassSignature$Token token : this.classBound) {
                sb.append(token.asType());
            }
        }
        if (this.ifaceBound != null) {
            for (ClassSignature$Token token : this.ifaceBound) {
                sb.append(':').append(token.asType());
            }
        }
        return sb.toString();
    }

    @Override
    public String asType() {
        return this.asType(false);
    }

    public String asType(boolean raw) {
        StringBuilder sb = new StringBuilder();
        if (this.array) {
            sb.append('[');
        }
        if (this.symbol != '\u0000') {
            sb.append(this.symbol);
        }
        if (this.type == null) {
            return sb.toString();
        }
        if (!this.inner) {
            sb.append('L');
        }
        sb.append(this.type);
        if (!raw) {
            if (this.signature != null) {
                sb.append('<');
                for (ClassSignature.IToken token : this.signature) {
                    sb.append(token.asType());
                }
                sb.append('>');
            }
            if (this.suffix != null) {
                for (ClassSignature.IToken token : this.suffix) {
                    sb.append('.').append(token.asType());
                }
            }
        }
        if (!this.inner) {
            sb.append(';');
        }
        return sb.toString();
    }

    boolean isRaw() {
        return this.signature == null;
    }

    String getClassType() {
        return this.type != null ? this.type : ClassSignature.OBJECT;
    }

    @Override
    public ClassSignature$Token asToken() {
        return this;
    }
}
