package org.spongepowered.asm.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Type {
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final int METHOD = 11;
    public static final Type VOID_TYPE = new Type(0, null, 0x56050000, 1);
    public static final Type BOOLEAN_TYPE = new Type(1, null, 1509950721, 1);
    public static final Type CHAR_TYPE = new Type(2, null, 1124075009, 1);
    public static final Type BYTE_TYPE = new Type(3, null, 1107297537, 1);
    public static final Type SHORT_TYPE = new Type(4, null, 1392510721, 1);
    public static final Type INT_TYPE = new Type(5, null, 1224736769, 1);
    public static final Type FLOAT_TYPE = new Type(6, null, 1174536705, 1);
    public static final Type LONG_TYPE = new Type(7, null, 1241579778, 1);
    public static final Type DOUBLE_TYPE = new Type(8, null, 1141048066, 1);
    private final int sort;
    private final char[] buf;
    private final int off;
    private final int len;

    private Type(int sort, char[] buf, int off, int len) {
        this.sort = sort;
        this.buf = buf;
        this.off = off;
        this.len = len;
    }

    public static Type getType(String typeDescriptor) {
        return Type.getType(typeDescriptor.toCharArray(), 0);
    }

    public static Type getObjectType(String internalName) {
        char[] buf = internalName.toCharArray();
        return new Type(buf[0] == '[' ? 9 : 10, buf, 0, buf.length);
    }

    public static Type getMethodType(String methodDescriptor) {
        return Type.getType(methodDescriptor.toCharArray(), 0);
    }

    public static Type getMethodType(Type returnType, Type ... argumentTypes) {
        return Type.getType(Type.getMethodDescriptor(returnType, argumentTypes));
    }

    public static Type getType(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == Integer.TYPE) {
                return INT_TYPE;
            }
            if (c == Void.TYPE) {
                return VOID_TYPE;
            }
            if (c == Boolean.TYPE) {
                return BOOLEAN_TYPE;
            }
            if (c == Byte.TYPE) {
                return BYTE_TYPE;
            }
            if (c == Character.TYPE) {
                return CHAR_TYPE;
            }
            if (c == Short.TYPE) {
                return SHORT_TYPE;
            }
            if (c == Double.TYPE) {
                return DOUBLE_TYPE;
            }
            if (c == Float.TYPE) {
                return FLOAT_TYPE;
            }
            return LONG_TYPE;
        }
        return Type.getType(Type.getDescriptor(c));
    }

    public static Type getType(Constructor<?> c) {
        return Type.getType(Type.getConstructorDescriptor(c));
    }

    public static Type getType(Method m) {
        return Type.getType(Type.getMethodDescriptor(m));
    }

    public static Type[] getArgumentTypes(String methodDescriptor) {
        char car;
        char[] buf = methodDescriptor.toCharArray();
        int off = 1;
        int size = 0;
        while ((car = buf[off++]) != ')') {
            if (car == 'L') {
                while (buf[off++] != ';') {
                }
                ++size;
                continue;
            }
            if (car == '[') continue;
            ++size;
        }
        Type[] args = new Type[size];
        off = 1;
        size = 0;
        while (buf[off] != ')') {
            args[size] = Type.getType(buf, off);
            off += args[size].len + (args[size].sort == 10 ? 2 : 0);
            ++size;
        }
        return args;
    }

    public static Type[] getArgumentTypes(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        Type[] types = new Type[classes.length];
        for (int i = classes.length - 1; i >= 0; --i) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }

    /*
     * Unable to fully structure code
     */
    public static Type getReturnType(String methodDescriptor) {
        buf = methodDescriptor.toCharArray();
        off = 1;
        block0: while (true) {
            if ((car = buf[off++]) == ')') {
                return Type.getType(buf, off);
            }
            if (car != 'L') continue;
            while (true) {
                if (buf[off++] != ';') ** break;
                continue block0;
            }
            break;
        }
    }

    public static Type getReturnType(Method method) {
        return Type.getType(method.getReturnType());
    }

    public static int getArgumentsAndReturnSizes(String desc) {
        int n = 1;
        int c = 1;
        while (true) {
            char car;
            if ((car = desc.charAt(c++)) == ')') {
                car = desc.charAt(c);
                return n << 2 | (car == 'V' ? 0 : (car == 'D' || car == 'J' ? 2 : 1));
            }
            if (car == 'L') {
                while (desc.charAt(c++) != ';') {
                }
                ++n;
                continue;
            }
            if (car == '[') {
                while ((car = desc.charAt(c)) == '[') {
                    ++c;
                }
                if (car != 'D' && car != 'J') continue;
                --n;
                continue;
            }
            if (car == 'D' || car == 'J') {
                n += 2;
                continue;
            }
            ++n;
        }
    }

    private static Type getType(char[] buf, int off) {
        switch (buf[off]) {
            case 'V': {
                return VOID_TYPE;
            }
            case 'Z': {
                return BOOLEAN_TYPE;
            }
            case 'C': {
                return CHAR_TYPE;
            }
            case 'B': {
                return BYTE_TYPE;
            }
            case 'S': {
                return SHORT_TYPE;
            }
            case 'I': {
                return INT_TYPE;
            }
            case 'F': {
                return FLOAT_TYPE;
            }
            case 'J': {
                return LONG_TYPE;
            }
            case 'D': {
                return DOUBLE_TYPE;
            }
            case '[': {
                int len = 1;
                while (buf[off + len] == '[') {
                    ++len;
                }
                if (buf[off + len] == 'L') {
                    ++len;
                    while (buf[off + len] != ';') {
                        ++len;
                    }
                }
                return new Type(9, buf, off, len + 1);
            }
            case 'L': {
                int len = 1;
                while (buf[off + len] != ';') {
                    ++len;
                }
                return new Type(10, buf, off + 1, len - 1);
            }
        }
        return new Type(11, buf, off, buf.length - off);
    }

    public int getSort() {
        return this.sort;
    }

    public int getDimensions() {
        int i = 1;
        while (this.buf[this.off + i] == '[') {
            ++i;
        }
        return i;
    }

    public Type getElementType() {
        return Type.getType(this.buf, this.off + this.getDimensions());
    }

    public String getClassName() {
        switch (this.sort) {
            case 0: {
                return "void";
            }
            case 1: {
                return "boolean";
            }
            case 2: {
                return "char";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 5: {
                return "int";
            }
            case 6: {
                return "float";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "double";
            }
            case 9: {
                StringBuilder sb = new StringBuilder(this.getElementType().getClassName());
                for (int i = this.getDimensions(); i > 0; --i) {
                    sb.append("[]");
                }
                return sb.toString();
            }
            case 10: {
                return new String(this.buf, this.off, this.len).replace('/', '.');
            }
        }
        return null;
    }

    public String getInternalName() {
        return new String(this.buf, this.off, this.len);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.getDescriptor());
    }

    public Type getReturnType() {
        return Type.getReturnType(this.getDescriptor());
    }

    public int getArgumentsAndReturnSizes() {
        return Type.getArgumentsAndReturnSizes(this.getDescriptor());
    }

    public String getDescriptor() {
        StringBuilder buf = new StringBuilder();
        this.getDescriptor(buf);
        return buf.toString();
    }

    public static String getMethodDescriptor(Type returnType, Type ... argumentTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (int i = 0; i < argumentTypes.length; ++i) {
            argumentTypes[i].getDescriptor(buf);
        }
        buf.append(')');
        returnType.getDescriptor(buf);
        return buf.toString();
    }

    private void getDescriptor(StringBuilder buf) {
        if (this.buf == null) {
            buf.append((char)((this.off & 0xFF000000) >>> 24));
        } else if (this.sort == 10) {
            buf.append('L');
            buf.append(this.buf, this.off, this.len);
            buf.append(';');
        } else {
            buf.append(this.buf, this.off, this.len);
        }
    }

    public static String getInternalName(Class<?> c) {
        return c.getName().replace('.', '/');
    }

    public static String getDescriptor(Class<?> c) {
        StringBuilder buf = new StringBuilder();
        Type.getDescriptor(buf, c);
        return buf.toString();
    }

    public static String getConstructorDescriptor(Constructor<?> c) {
        Class<?>[] parameters = c.getParameterTypes();
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (int i = 0; i < parameters.length; ++i) {
            Type.getDescriptor(buf, parameters[i]);
        }
        return buf.append(")V").toString();
    }

    public static String getMethodDescriptor(Method m) {
        Class<?>[] parameters = m.getParameterTypes();
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (int i = 0; i < parameters.length; ++i) {
            Type.getDescriptor(buf, parameters[i]);
        }
        buf.append(')');
        Type.getDescriptor(buf, m.getReturnType());
        return buf.toString();
    }

    private static void getDescriptor(StringBuilder buf, Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                int car = d == Integer.TYPE ? 73 : (d == Void.TYPE ? 86 : (d == Boolean.TYPE ? 90 : (d == Byte.TYPE ? 66 : (d == Character.TYPE ? 67 : (d == Short.TYPE ? 83 : (d == Double.TYPE ? 68 : (d == Float.TYPE ? 70 : 74)))))));
                buf.append((char)car);
                return;
            }
            if (!d.isArray()) break;
            buf.append('[');
            d = d.getComponentType();
        }
        buf.append('L');
        String name = d.getName();
        int len = name.length();
        for (int i = 0; i < len; ++i) {
            char car = name.charAt(i);
            buf.append(car == '.' ? (char)'/' : (char)car);
        }
        buf.append(';');
    }

    public int getSize() {
        return this.buf == null ? this.off & 0xFF : 1;
    }

    public int getOpcode(int opcode) {
        if (opcode == 46 || opcode == 79) {
            return opcode + (this.buf == null ? (this.off & 0xFF00) >> 8 : 4);
        }
        return opcode + (this.buf == null ? (this.off & 0xFF0000) >> 16 : 4);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Type)) {
            return false;
        }
        Type t = (Type)o;
        if (this.sort != t.sort) {
            return false;
        }
        if (this.sort >= 9) {
            if (this.len != t.len) {
                return false;
            }
            int i = this.off;
            int j = t.off;
            int end = i + this.len;
            while (i < end) {
                if (this.buf[i] != t.buf[j]) {
                    return false;
                }
                ++i;
                ++j;
            }
        }
        return true;
    }

    public int hashCode() {
        int hc = 13 * this.sort;
        if (this.sort >= 9) {
            int i;
            int end = i + this.len;
            for (i = this.off; i < end; ++i) {
                hc = 17 * (hc + this.buf[i]);
            }
        }
        return hc;
    }

    public String toString() {
        return this.getDescriptor();
    }
}
