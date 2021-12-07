package org.spongepowered.asm.lib.commons;

import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.commons.SignatureRemapper;
import org.spongepowered.asm.lib.signature.SignatureReader;
import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.lib.signature.SignatureWriter;

public abstract class Remapper {
    public String mapDesc(String desc) {
        Type t = Type.getType(desc);
        switch (t.getSort()) {
            case 9: {
                String s = this.mapDesc(t.getElementType().getDescriptor());
                for (int i = 0; i < t.getDimensions(); ++i) {
                    s = '[' + s;
                }
                return s;
            }
            case 10: {
                String newType = this.map(t.getInternalName());
                if (newType == null) break;
                return 'L' + newType + ';';
            }
        }
        return desc;
    }

    private Type mapType(Type t) {
        switch (t.getSort()) {
            case 9: {
                String s = this.mapDesc(t.getElementType().getDescriptor());
                for (int i = 0; i < t.getDimensions(); ++i) {
                    s = '[' + s;
                }
                return Type.getType(s);
            }
            case 10: {
                String s = this.map(t.getInternalName());
                return s != null ? Type.getObjectType(s) : t;
            }
            case 11: {
                return Type.getMethodType(this.mapMethodDesc(t.getDescriptor()));
            }
        }
        return t;
    }

    public String mapType(String type) {
        if (type == null) {
            return null;
        }
        return this.mapType(Type.getObjectType(type)).getInternalName();
    }

    public String[] mapTypes(String[] types) {
        String[] newTypes = null;
        boolean needMapping = false;
        for (int i = 0; i < types.length; ++i) {
            String type = types[i];
            String newType = this.map(type);
            if (newType != null && newTypes == null) {
                newTypes = new String[types.length];
                if (i > 0) {
                    System.arraycopy(types, 0, newTypes, 0, i);
                }
                needMapping = true;
            }
            if (!needMapping) continue;
            newTypes[i] = newType == null ? type : newType;
        }
        return needMapping ? newTypes : types;
    }

    public String mapMethodDesc(String desc) {
        if ("()V".equals(desc)) {
            return desc;
        }
        Type[] args = Type.getArgumentTypes(desc);
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < args.length; ++i) {
            sb.append(this.mapDesc(args[i].getDescriptor()));
        }
        Type returnType = Type.getReturnType(desc);
        if (returnType == Type.VOID_TYPE) {
            sb.append(")V");
            return sb.toString();
        }
        sb.append(')').append(this.mapDesc(returnType.getDescriptor()));
        return sb.toString();
    }

    public Object mapValue(Object value) {
        if (value instanceof Type) {
            return this.mapType((Type)value);
        }
        if (value instanceof Handle) {
            Handle h = (Handle)value;
            return new Handle(h.getTag(), this.mapType(h.getOwner()), this.mapMethodName(h.getOwner(), h.getName(), h.getDesc()), this.mapMethodDesc(h.getDesc()), h.isInterface());
        }
        return value;
    }

    public String mapSignature(String signature, boolean typeSignature) {
        if (signature == null) {
            return null;
        }
        SignatureReader r = new SignatureReader(signature);
        SignatureWriter w = new SignatureWriter();
        SignatureVisitor a = this.createSignatureRemapper(w);
        if (typeSignature) {
            r.acceptType(a);
        } else {
            r.accept(a);
        }
        return w.toString();
    }

    @Deprecated
    protected SignatureVisitor createRemappingSignatureAdapter(SignatureVisitor v) {
        return new SignatureRemapper(v, this);
    }

    protected SignatureVisitor createSignatureRemapper(SignatureVisitor v) {
        return this.createRemappingSignatureAdapter(v);
    }

    public String mapMethodName(String owner, String name, String desc) {
        return name;
    }

    public String mapInvokeDynamicMethodName(String name, String desc) {
        return name;
    }

    public String mapFieldName(String owner, String name, String desc) {
        return name;
    }

    public String map(String typeName) {
        return typeName;
    }
}
