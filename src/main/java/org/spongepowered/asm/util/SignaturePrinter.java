/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package org.spongepowered.asm.util;

import com.google.common.base.Strings;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;

public class SignaturePrinter {
    private final String name;
    private final Type returnType;
    private final Type[] argTypes;
    private final String[] argNames;
    private String modifiers = "private void";
    private boolean fullyQualified;

    public SignaturePrinter(MethodNode method) {
        this(method.name, Type.VOID_TYPE, Type.getArgumentTypes(method.desc));
        this.setModifiers(method);
    }

    public SignaturePrinter(MethodNode method, String[] argNames) {
        this(method.name, Type.VOID_TYPE, Type.getArgumentTypes(method.desc), argNames);
        this.setModifiers(method);
    }

    public SignaturePrinter(MemberInfo member) {
        this(member.name, member.desc);
    }

    public SignaturePrinter(String name, String desc) {
        this(name, Type.getReturnType(desc), Type.getArgumentTypes(desc));
    }

    public SignaturePrinter(String name, Type returnType, Type[] args) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = new Type[args.length];
        this.argNames = new String[args.length];
        int v = 0;
        for (int l = 0; l < args.length; ++l) {
            if (args[l] == null) continue;
            this.argTypes[l] = args[l];
            this.argNames[l] = "var" + v++;
        }
    }

    public SignaturePrinter(String name, Type returnType, LocalVariableNode[] args) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = new Type[args.length];
        this.argNames = new String[args.length];
        for (int l = 0; l < args.length; ++l) {
            if (args[l] == null) continue;
            this.argTypes[l] = Type.getType(args[l].desc);
            this.argNames[l] = args[l].name;
        }
    }

    public SignaturePrinter(String name, Type returnType, Type[] argTypes, String[] argNames) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.argNames = argNames;
        if (this.argTypes.length > this.argNames.length) {
            throw new IllegalArgumentException(String.format("Types array length must not exceed names array length! (names=%d, types=%d)", this.argNames.length, this.argTypes.length));
        }
    }

    public String getFormattedArgs() {
        return this.appendArgs(new StringBuilder(), true, true).toString();
    }

    public String getReturnType() {
        return SignaturePrinter.getTypeName(this.returnType, false, this.fullyQualified);
    }

    public void setModifiers(MethodNode method) {
        String returnType = SignaturePrinter.getTypeName(Type.getReturnType(method.desc), false, this.fullyQualified);
        if ((method.access & 1) != 0) {
            this.setModifiers("public " + returnType);
        } else if ((method.access & 4) != 0) {
            this.setModifiers("protected " + returnType);
        } else if ((method.access & 2) != 0) {
            this.setModifiers("private " + returnType);
        } else {
            this.setModifiers(returnType);
        }
    }

    public SignaturePrinter setModifiers(String modifiers) {
        this.modifiers = modifiers.replace("${returnType}", this.getReturnType());
        return this;
    }

    public SignaturePrinter setFullyQualified(boolean fullyQualified) {
        this.fullyQualified = fullyQualified;
        return this;
    }

    public boolean isFullyQualified() {
        return this.fullyQualified;
    }

    public String toString() {
        return this.appendArgs(new StringBuilder().append(this.modifiers).append(" ").append(this.name), false, true).toString();
    }

    public String toDescriptor() {
        StringBuilder args = this.appendArgs(new StringBuilder(), true, false);
        return args.append(SignaturePrinter.getTypeName(this.returnType, false, this.fullyQualified)).toString();
    }

    private StringBuilder appendArgs(StringBuilder sb, boolean typesOnly, boolean pretty) {
        sb.append('(');
        for (int var = 0; var < this.argTypes.length; ++var) {
            if (this.argTypes[var] == null) continue;
            if (var > 0) {
                sb.append(',');
                if (pretty) {
                    sb.append(' ');
                }
            }
            try {
                String name = typesOnly ? null : (Strings.isNullOrEmpty((String)this.argNames[var]) ? "unnamed" + var : this.argNames[var]);
                this.appendType(sb, this.argTypes[var], name);
                continue;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return sb.append(")");
    }

    private StringBuilder appendType(StringBuilder sb, Type type, String name) {
        switch (type.getSort()) {
            case 9: {
                return SignaturePrinter.appendArraySuffix(this.appendType(sb, type.getElementType(), name), type);
            }
            case 10: {
                return this.appendType(sb, type.getClassName(), name);
            }
        }
        sb.append(SignaturePrinter.getTypeName(type, false, this.fullyQualified));
        if (name != null) {
            sb.append(' ').append(name);
        }
        return sb;
    }

    private StringBuilder appendType(StringBuilder sb, String typeName, String name) {
        if (!this.fullyQualified) {
            typeName = typeName.substring(typeName.lastIndexOf(46) + 1);
        }
        sb.append(typeName);
        if (typeName.endsWith("CallbackInfoReturnable")) {
            sb.append('<').append(SignaturePrinter.getTypeName(this.returnType, true, this.fullyQualified)).append('>');
        }
        if (name != null) {
            sb.append(' ').append(name);
        }
        return sb;
    }

    public static String getTypeName(Type type, boolean box) {
        return SignaturePrinter.getTypeName(type, box, false);
    }

    public static String getTypeName(Type type, boolean box, boolean fullyQualified) {
        switch (type.getSort()) {
            case 0: {
                return box ? "Void" : "void";
            }
            case 1: {
                return box ? "Boolean" : "boolean";
            }
            case 2: {
                return box ? "Character" : "char";
            }
            case 3: {
                return box ? "Byte" : "byte";
            }
            case 4: {
                return box ? "Short" : "short";
            }
            case 5: {
                return box ? "Integer" : "int";
            }
            case 6: {
                return box ? "Float" : "float";
            }
            case 7: {
                return box ? "Long" : "long";
            }
            case 8: {
                return box ? "Double" : "double";
            }
            case 9: {
                return SignaturePrinter.getTypeName(type.getElementType(), box, fullyQualified) + SignaturePrinter.arraySuffix(type);
            }
            case 10: {
                String typeName = type.getClassName();
                if (!fullyQualified) {
                    typeName = typeName.substring(typeName.lastIndexOf(46) + 1);
                }
                return typeName;
            }
        }
        return "Object";
    }

    private static String arraySuffix(Type type) {
        return Strings.repeat((String)"[]", (int)type.getDimensions());
    }

    private static StringBuilder appendArraySuffix(StringBuilder sb, Type type) {
        for (int i = 0; i < type.getDimensions(); ++i) {
            sb.append("[]");
        }
        return sb;
    }
}

