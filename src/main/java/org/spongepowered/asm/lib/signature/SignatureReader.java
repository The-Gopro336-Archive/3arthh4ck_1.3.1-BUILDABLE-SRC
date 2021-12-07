package org.spongepowered.asm.lib.signature;

import org.spongepowered.asm.lib.signature.SignatureVisitor;

public class SignatureReader {
    private final String signature;

    public SignatureReader(String signature) {
        this.signature = signature;
    }

    public void accept(SignatureVisitor v) {
        int pos;
        String signature = this.signature;
        int len = signature.length();
        if (signature.charAt(0) == '<') {
            char c;
            pos = 2;
            do {
                int end = signature.indexOf(58, pos);
                v.visitFormalTypeParameter(signature.substring(pos - 1, end));
                pos = end + 1;
                c = signature.charAt(pos);
                if (c == 'L' || c == '[' || c == 'T') {
                    pos = SignatureReader.parseType(signature, pos, v.visitClassBound());
                }
                while ((c = signature.charAt(pos++)) == ':') {
                    pos = SignatureReader.parseType(signature, pos, v.visitInterfaceBound());
                }
            } while (c != '>');
        } else {
            pos = 0;
        }
        if (signature.charAt(pos) == '(') {
            ++pos;
            while (signature.charAt(pos) != ')') {
                pos = SignatureReader.parseType(signature, pos, v.visitParameterType());
            }
            pos = SignatureReader.parseType(signature, pos + 1, v.visitReturnType());
            while (pos < len) {
                pos = SignatureReader.parseType(signature, pos + 1, v.visitExceptionType());
            }
        } else {
            pos = SignatureReader.parseType(signature, pos, v.visitSuperclass());
            while (pos < len) {
                pos = SignatureReader.parseType(signature, pos, v.visitInterface());
            }
        }
    }

    public void acceptType(SignatureVisitor v) {
        SignatureReader.parseType(this.signature, 0, v);
    }

    private static int parseType(String signature, int pos, SignatureVisitor v) {
        char c = signature.charAt(pos++);
        switch (c) {
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'V': 
            case 'Z': {
                v.visitBaseType(c);
                return pos;
            }
            case '[': {
                return SignatureReader.parseType(signature, pos, v.visitArrayType());
            }
            case 'T': {
                int end = signature.indexOf(59, pos);
                v.visitTypeVariable(signature.substring(pos, end));
                return end + 1;
            }
        }
        int start = pos;
        boolean visited = false;
        boolean inner = false;
        while (true) {
            c = signature.charAt(pos++);
            block5 : switch (c) {
                case '.': 
                case ';': {
                    String name;
                    if (!visited) {
                        name = signature.substring(start, pos - 1);
                        if (inner) {
                            v.visitInnerClassType(name);
                        } else {
                            v.visitClassType(name);
                        }
                    }
                    if (c == ';') {
                        v.visitEnd();
                        return pos;
                    }
                    start = pos;
                    visited = false;
                    inner = true;
                    break;
                }
                case '<': {
                    String name = signature.substring(start, pos - 1);
                    if (inner) {
                        v.visitInnerClassType(name);
                    } else {
                        v.visitClassType(name);
                    }
                    visited = true;
                    block15: while (true) {
                        c = signature.charAt(pos);
                        switch (c) {
                            case '>': {
                                break block5;
                            }
                            case '*': {
                                ++pos;
                                v.visitTypeArgument();
                                continue block15;
                            }
                            case '+': 
                            case '-': {
                                pos = SignatureReader.parseType(signature, pos + 1, v.visitTypeArgument(c));
                                continue block15;
                            }
                        }
                        pos = SignatureReader.parseType(signature, pos, v.visitTypeArgument('='));
                    }
                }
            }
        }
    }
}
