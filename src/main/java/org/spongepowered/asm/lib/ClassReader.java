/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib;

import java.io.IOException;
import java.io.InputStream;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Context;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.MethodWriter;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

public class ClassReader {
    static final boolean SIGNATURES = true;
    static final boolean ANNOTATIONS = true;
    static final boolean FRAMES = true;
    static final boolean WRITER = true;
    static final boolean RESIZE = true;
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    static final int EXPAND_ASM_INSNS = 256;
    public final byte[] b;
    private final int[] items;
    private final String[] strings;
    private final int maxStringLength;
    public final int header;

    public ClassReader(byte[] b) {
        this(b, 0, b.length);
    }

    public ClassReader(byte[] b, int off, int len) {
        this.b = b;
        if (this.readShort(off + 6) > 52) {
            throw new IllegalArgumentException();
        }
        this.items = new int[this.readUnsignedShort(off + 8)];
        int n = this.items.length;
        this.strings = new String[n];
        int max = 0;
        int index = off + 10;
        for (int i = 1; i < n; ++i) {
            int size;
            this.items[i] = index + 1;
            switch (b[index]) {
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 18: {
                    size = 5;
                    break;
                }
                case 5: 
                case 6: {
                    size = 9;
                    ++i;
                    break;
                }
                case 1: {
                    size = 3 + this.readUnsignedShort(index + 1);
                    if (size <= max) break;
                    max = size;
                    break;
                }
                case 15: {
                    size = 4;
                    break;
                }
                default: {
                    size = 3;
                }
            }
            index += size;
        }
        this.maxStringLength = max;
        this.header = index;
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.maxStringLength]);
    }

    public String getSuperName() {
        return this.readClass(this.header + 4, new char[this.maxStringLength]);
    }

    public String[] getInterfaces() {
        int index = this.header + 6;
        int n = this.readUnsignedShort(index);
        String[] interfaces = new String[n];
        if (n > 0) {
            char[] buf = new char[this.maxStringLength];
            for (int i = 0; i < n; ++i) {
                interfaces[i] = this.readClass(index += 2, buf);
            }
        }
        return interfaces;
    }

    void copyPool(ClassWriter classWriter) {
        char[] buf = new char[this.maxStringLength];
        int ll = this.items.length;
        Item[] items2 = new Item[ll];
        for (int i = 1; i < ll; ++i) {
            int index = this.items[i];
            byte tag = this.b[index - 1];
            Item item = new Item(i);
            switch (tag) {
                case 9: 
                case 10: 
                case 11: {
                    int nameType = this.items[this.readUnsignedShort(index + 2)];
                    item.set(tag, this.readClass(index, buf), this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf));
                    break;
                }
                case 3: {
                    item.set(this.readInt(index));
                    break;
                }
                case 4: {
                    item.set(Float.intBitsToFloat(this.readInt(index)));
                    break;
                }
                case 12: {
                    item.set(tag, this.readUTF8(index, buf), this.readUTF8(index + 2, buf), null);
                    break;
                }
                case 5: {
                    item.set(this.readLong(index));
                    ++i;
                    break;
                }
                case 6: {
                    item.set(Double.longBitsToDouble(this.readLong(index)));
                    ++i;
                    break;
                }
                case 1: {
                    String s = this.strings[i];
                    if (s == null) {
                        index = this.items[i];
                        s = this.strings[i] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
                    }
                    item.set(tag, s, null, null);
                    break;
                }
                case 15: {
                    int fieldOrMethodRef = this.items[this.readUnsignedShort(index + 1)];
                    int nameType = this.items[this.readUnsignedShort(fieldOrMethodRef + 2)];
                    item.set(20 + this.readByte(index), this.readClass(fieldOrMethodRef, buf), this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf));
                    break;
                }
                case 18: {
                    if (classWriter.bootstrapMethods == null) {
                        this.copyBootstrapMethods(classWriter, items2, buf);
                    }
                    int nameType = this.items[this.readUnsignedShort(index + 2)];
                    item.set(this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf), this.readUnsignedShort(index));
                    break;
                }
                default: {
                    item.set(tag, this.readUTF8(index, buf), null, null);
                }
            }
            int index2 = item.hashCode % items2.length;
            item.next = items2[index2];
            items2[index2] = item;
        }
        int off = this.items[1] - 1;
        classWriter.pool.putByteArray(this.b, off, this.header - off);
        classWriter.items = items2;
        classWriter.threshold = (int)(0.75 * (double)ll);
        classWriter.index = ll;
    }

    private void copyBootstrapMethods(ClassWriter classWriter, Item[] items, char[] c) {
        int u = this.getAttributes();
        boolean found = false;
        for (int i = this.readUnsignedShort(u); i > 0; --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("BootstrapMethods".equals(attrName)) {
                found = true;
                break;
            }
            u += 6 + this.readInt(u + 4);
        }
        if (!found) {
            return;
        }
        int boostrapMethodCount = this.readUnsignedShort(u + 8);
        int v = u + 10;
        for (int j = 0; j < boostrapMethodCount; ++j) {
            int position = v - u - 10;
            int hashCode = this.readConst(this.readUnsignedShort(v), c).hashCode();
            for (int k = this.readUnsignedShort(v + 2); k > 0; --k) {
                hashCode ^= this.readConst(this.readUnsignedShort(v + 4), c).hashCode();
                v += 2;
            }
            v += 4;
            Item item = new Item(j);
            item.set(position, hashCode & Integer.MAX_VALUE);
            int index = item.hashCode % items.length;
            item.next = items[index];
            items[index] = item;
        }
        int attrSize = this.readInt(u + 4);
        ByteVector bootstrapMethods = new ByteVector(attrSize + 62);
        bootstrapMethods.putByteArray(this.b, u + 10, attrSize - 2);
        classWriter.bootstrapMethodsCount = boostrapMethodCount;
        classWriter.bootstrapMethods = bootstrapMethods;
    }

    public ClassReader(InputStream is) throws IOException {
        this(ClassReader.readClass(is, false));
    }

    public ClassReader(String name) throws IOException {
        this(ClassReader.readClass(ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class"), true));
    }

    private static byte[] readClass(InputStream is, boolean close) throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true) {
                int n;
                if ((n = is.read(b, len, b.length - len)) == -1) {
                    byte[] c;
                    if (len < b.length) {
                        c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    c = b;
                    return c;
                }
                if ((len += n) != b.length) continue;
                int last = is.read();
                if (last < 0) {
                    byte[] arrby = b;
                    return arrby;
                }
                byte[] c = new byte[b.length + 1000];
                System.arraycopy(b, 0, c, 0, len);
                c[len++] = (byte)last;
                b = c;
            }
        }
        finally {
            if (close) {
                is.close();
            }
        }
    }

    public void accept(ClassVisitor classVisitor, int flags) {
        this.accept(classVisitor, new Attribute[0], flags);
    }

    public void accept(ClassVisitor classVisitor, Attribute[] attrs, int flags) {
        int i;
        int u = this.header;
        char[] c = new char[this.maxStringLength];
        Context context = new Context();
        context.attrs = attrs;
        context.flags = flags;
        context.buffer = c;
        int access = this.readUnsignedShort(u);
        String name = this.readClass(u + 2, c);
        String superClass = this.readClass(u + 4, c);
        String[] interfaces = new String[this.readUnsignedShort(u + 6)];
        u += 8;
        for (int i2 = 0; i2 < interfaces.length; ++i2) {
            interfaces[i2] = this.readClass(u, c);
            u += 2;
        }
        String signature = null;
        String sourceFile = null;
        String sourceDebug = null;
        String enclosingOwner = null;
        String enclosingName = null;
        String enclosingDesc = null;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        int innerClasses = 0;
        Attribute attributes = null;
        u = this.getAttributes();
        for (i = this.readUnsignedShort(u); i > 0; --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("SourceFile".equals(attrName)) {
                sourceFile = this.readUTF8(u + 8, c);
            } else if ("InnerClasses".equals(attrName)) {
                innerClasses = u + 8;
            } else if ("EnclosingMethod".equals(attrName)) {
                enclosingOwner = this.readClass(u + 8, c);
                int item = this.readUnsignedShort(u + 10);
                if (item != 0) {
                    enclosingName = this.readUTF8(this.items[item], c);
                    enclosingDesc = this.readUTF8(this.items[item] + 2, c);
                }
            } else if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
            } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
            } else if ("Deprecated".equals(attrName)) {
                access |= 0x20000;
            } else if ("Synthetic".equals(attrName)) {
                access |= 0x41000;
            } else if ("SourceDebugExtension".equals(attrName)) {
                int len = this.readInt(u + 4);
                sourceDebug = this.readUTF(u + 8, len, new char[len]);
            } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
            } else if ("BootstrapMethods".equals(attrName)) {
                int[] bootstrapMethods = new int[this.readUnsignedShort(u + 8)];
                int v = u + 10;
                for (int j = 0; j < bootstrapMethods.length; ++j) {
                    bootstrapMethods[j] = v;
                    v += 2 + this.readUnsignedShort(v + 2) << 1;
                }
                context.bootstrapMethods = bootstrapMethods;
            } else {
                Attribute attr = this.readAttribute(attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
                if (attr != null) {
                    attr.next = attributes;
                    attributes = attr;
                }
            }
            u += 6 + this.readInt(u + 4);
        }
        classVisitor.visit(this.readInt(this.items[1] - 7), access, name, signature, superClass, interfaces);
        if ((flags & 2) == 0 && (sourceFile != null || sourceDebug != null)) {
            classVisitor.visitSource(sourceFile, sourceDebug);
        }
        if (enclosingOwner != null) {
            classVisitor.visitOuterClass(enclosingOwner, enclosingName, enclosingDesc);
        }
        if (anns != 0) {
            int v = anns + 2;
            for (i = this.readUnsignedShort(anns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitAnnotation(this.readUTF8(v, c), true));
            }
        }
        if (ianns != 0) {
            int v = ianns + 2;
            for (i = this.readUnsignedShort(ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            int v = tanns + 2;
            for (i = this.readUnsignedShort(tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            int v = itanns + 2;
            for (i = this.readUnsignedShort(itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            classVisitor.visitAttribute(attributes);
            attributes = attr;
        }
        if (innerClasses != 0) {
            int v = innerClasses + 2;
            for (int i3 = this.readUnsignedShort(innerClasses); i3 > 0; --i3) {
                classVisitor.visitInnerClass(this.readClass(v, c), this.readClass(v + 2, c), this.readUTF8(v + 4, c), this.readUnsignedShort(v + 6));
                v += 8;
            }
        }
        u = this.header + 10 + 2 * interfaces.length;
        for (i = this.readUnsignedShort(u - 2); i > 0; --i) {
            u = this.readField(classVisitor, context, u);
        }
        for (i = this.readUnsignedShort((u += 2) - 2); i > 0; --i) {
            u = this.readMethod(classVisitor, context, u);
        }
        classVisitor.visitEnd();
    }

    private int readField(ClassVisitor classVisitor, Context context, int u) {
        int v;
        char[] c = context.buffer;
        int access = this.readUnsignedShort(u);
        String name = this.readUTF8(u + 2, c);
        String desc = this.readUTF8(u + 4, c);
        String signature = null;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        Object value = null;
        Attribute attributes = null;
        for (int i = this.readUnsignedShort(u += 6); i > 0; --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("ConstantValue".equals(attrName)) {
                int item = this.readUnsignedShort(u + 8);
                value = item == 0 ? null : this.readConst(item, c);
            } else if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
            } else if ("Deprecated".equals(attrName)) {
                access |= 0x20000;
            } else if ("Synthetic".equals(attrName)) {
                access |= 0x41000;
            } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
            } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
            } else {
                Attribute attr = this.readAttribute(context.attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
                if (attr != null) {
                    attr.next = attributes;
                    attributes = attr;
                }
            }
            u += 6 + this.readInt(u + 4);
        }
        u += 2;
        FieldVisitor fv = classVisitor.visitField(access, name, desc, signature, value);
        if (fv == null) {
            return u;
        }
        if (anns != 0) {
            v = anns + 2;
            for (int i = this.readUnsignedShort(anns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, fv.visitAnnotation(this.readUTF8(v, c), true));
            }
        }
        if (ianns != 0) {
            v = ianns + 2;
            for (int i = this.readUnsignedShort(ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, fv.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            v = tanns + 2;
            for (int i = this.readUnsignedShort(tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, fv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            v = itanns + 2;
            for (int i = this.readUnsignedShort(itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, fv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            fv.visitAttribute(attributes);
            attributes = attr;
        }
        fv.visitEnd();
        return u;
    }

    private int readMethod(ClassVisitor classVisitor, Context context, int u) {
        int v;
        char[] c = context.buffer;
        context.access = this.readUnsignedShort(u);
        context.name = this.readUTF8(u + 2, c);
        context.desc = this.readUTF8(u + 4, c);
        int code = 0;
        int exception = 0;
        String[] exceptions = null;
        String signature = null;
        int methodParameters = 0;
        int anns = 0;
        int ianns = 0;
        int tanns = 0;
        int itanns = 0;
        int dann = 0;
        int mpanns = 0;
        int impanns = 0;
        int firstAttribute = u += 6;
        Attribute attributes = null;
        for (int i = this.readUnsignedShort(u); i > 0; --i) {
            String attrName = this.readUTF8(u + 2, c);
            if ("Code".equals(attrName)) {
                if ((context.flags & 1) == 0) {
                    code = u + 8;
                }
            } else if ("Exceptions".equals(attrName)) {
                exceptions = new String[this.readUnsignedShort(u + 8)];
                exception = u + 10;
                for (int j = 0; j < exceptions.length; ++j) {
                    exceptions[j] = this.readClass(exception, c);
                    exception += 2;
                }
            } else if ("Signature".equals(attrName)) {
                signature = this.readUTF8(u + 8, c);
            } else if ("Deprecated".equals(attrName)) {
                context.access |= 0x20000;
            } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
                anns = u + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = u + 8;
            } else if ("AnnotationDefault".equals(attrName)) {
                dann = u + 8;
            } else if ("Synthetic".equals(attrName)) {
                context.access |= 0x41000;
            } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
                ianns = u + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = u + 8;
            } else if ("RuntimeVisibleParameterAnnotations".equals(attrName)) {
                mpanns = u + 8;
            } else if ("RuntimeInvisibleParameterAnnotations".equals(attrName)) {
                impanns = u + 8;
            } else if ("MethodParameters".equals(attrName)) {
                methodParameters = u + 8;
            } else {
                Attribute attr = this.readAttribute(context.attrs, attrName, u + 8, this.readInt(u + 4), c, -1, null);
                if (attr != null) {
                    attr.next = attributes;
                    attributes = attr;
                }
            }
            u += 6 + this.readInt(u + 4);
        }
        u += 2;
        MethodVisitor mv = classVisitor.visitMethod(context.access, context.name, context.desc, signature, exceptions);
        if (mv == null) {
            return u;
        }
        if (mv instanceof MethodWriter) {
            MethodWriter mw = (MethodWriter)mv;
            if (mw.cw.cr == this && signature == mw.signature) {
                boolean sameExceptions = false;
                if (exceptions == null) {
                    sameExceptions = mw.exceptionCount == 0;
                } else if (exceptions.length == mw.exceptionCount) {
                    sameExceptions = true;
                    for (int j = exceptions.length - 1; j >= 0; --j) {
                        if (mw.exceptions[j] == this.readUnsignedShort(exception -= 2)) continue;
                        sameExceptions = false;
                        break;
                    }
                }
                if (sameExceptions) {
                    mw.classReaderOffset = firstAttribute;
                    mw.classReaderLength = u - firstAttribute;
                    return u;
                }
            }
        }
        if (methodParameters != 0) {
            int i = this.b[methodParameters] & 0xFF;
            v = methodParameters + 1;
            while (i > 0) {
                mv.visitParameter(this.readUTF8(v, c), this.readUnsignedShort(v + 2));
                --i;
                v += 4;
            }
        }
        if (dann != 0) {
            AnnotationVisitor dv = mv.visitAnnotationDefault();
            this.readAnnotationValue(dann, c, null, dv);
            if (dv != null) {
                dv.visitEnd();
            }
        }
        if (anns != 0) {
            v = anns + 2;
            for (int i = this.readUnsignedShort(anns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, mv.visitAnnotation(this.readUTF8(v, c), true));
            }
        }
        if (ianns != 0) {
            v = ianns + 2;
            for (int i = this.readUnsignedShort(ianns); i > 0; --i) {
                v = this.readAnnotationValues(v + 2, c, true, mv.visitAnnotation(this.readUTF8(v, c), false));
            }
        }
        if (tanns != 0) {
            v = tanns + 2;
            for (int i = this.readUnsignedShort(tanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
            }
        }
        if (itanns != 0) {
            v = itanns + 2;
            for (int i = this.readUnsignedShort(itanns); i > 0; --i) {
                v = this.readAnnotationTarget(context, v);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitTypeAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
            }
        }
        if (mpanns != 0) {
            this.readParameterAnnotations(mv, context, mpanns, true);
        }
        if (impanns != 0) {
            this.readParameterAnnotations(mv, context, impanns, false);
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            mv.visitAttribute(attributes);
            attributes = attr;
        }
        if (code != 0) {
            mv.visitCode();
            this.readCode(mv, context, code);
        }
        mv.visitEnd();
        return u;
    }

    private void readCode(MethodVisitor mv, Context context, int u) {
        int i;
        byte[] b = this.b;
        char[] c = context.buffer;
        int maxStack = this.readUnsignedShort(u);
        int maxLocals = this.readUnsignedShort(u + 2);
        int codeLength = this.readInt(u + 4);
        int codeStart = u += 8;
        int codeEnd = u + codeLength;
        context.labels = new Label[codeLength + 2];
        Label[] labels = context.labels;
        this.readLabel(codeLength + 1, labels);
        block31: while (u < codeEnd) {
            int offset = u - codeStart;
            int opcode = b[u] & 0xFF;
            switch (ClassWriter.TYPE[opcode]) {
                case 0: 
                case 4: {
                    ++u;
                    continue block31;
                }
                case 9: {
                    this.readLabel(offset + this.readShort(u + 1), labels);
                    u += 3;
                    continue block31;
                }
                case 18: {
                    this.readLabel(offset + this.readUnsignedShort(u + 1), labels);
                    u += 3;
                    continue block31;
                }
                case 10: {
                    this.readLabel(offset + this.readInt(u + 1), labels);
                    u += 5;
                    continue block31;
                }
                case 17: {
                    opcode = b[u + 1] & 0xFF;
                    if (opcode == 132) {
                        u += 6;
                        continue block31;
                    }
                    u += 4;
                    continue block31;
                }
                case 14: {
                    int i2;
                    u = u + 4 - (offset & 3);
                    this.readLabel(offset + this.readInt(u), labels);
                    for (i2 = this.readInt(u + 8) - this.readInt(u + 4) + 1; i2 > 0; --i2) {
                        this.readLabel(offset + this.readInt(u + 12), labels);
                        u += 4;
                    }
                    u += 12;
                    continue block31;
                }
                case 15: {
                    int i2;
                    u = u + 4 - (offset & 3);
                    this.readLabel(offset + this.readInt(u), labels);
                    for (i2 = this.readInt(u + 4); i2 > 0; --i2) {
                        this.readLabel(offset + this.readInt(u + 12), labels);
                        u += 8;
                    }
                    u += 8;
                    continue block31;
                }
                case 1: 
                case 3: 
                case 11: {
                    u += 2;
                    continue block31;
                }
                case 2: 
                case 5: 
                case 6: 
                case 12: 
                case 13: {
                    u += 3;
                    continue block31;
                }
                case 7: 
                case 8: {
                    u += 5;
                    continue block31;
                }
            }
            u += 4;
        }
        for (int i3 = this.readUnsignedShort(u); i3 > 0; --i3) {
            Label start = this.readLabel(this.readUnsignedShort(u + 2), labels);
            Label end = this.readLabel(this.readUnsignedShort(u + 4), labels);
            Label handler = this.readLabel(this.readUnsignedShort(u + 6), labels);
            String type = this.readUTF8(this.items[this.readUnsignedShort(u + 8)], c);
            mv.visitTryCatchBlock(start, end, handler, type);
            u += 8;
        }
        int[] tanns = null;
        int[] itanns = null;
        int tann = 0;
        int itann = 0;
        int ntoff = -1;
        int nitoff = -1;
        int varTable = 0;
        int varTypeTable = 0;
        boolean zip = true;
        boolean unzip = (context.flags & 8) != 0;
        int stackMap = 0;
        int stackMapSize = 0;
        int frameCount = 0;
        Context frame = null;
        Attribute attributes = null;
        for (i = this.readUnsignedShort(u += 2); i > 0; --i) {
            int label;
            int j;
            int v;
            String attrName = this.readUTF8(u + 2, c);
            if ("LocalVariableTable".equals(attrName)) {
                if ((context.flags & 2) == 0) {
                    varTable = u + 8;
                    v = u;
                    for (j = this.readUnsignedShort(u + 8); j > 0; --j) {
                        label = this.readUnsignedShort(v + 10);
                        if (labels[label] == null) {
                            this.readLabel((int)label, (Label[])labels).status |= 1;
                        }
                        if (labels[label += this.readUnsignedShort(v + 12)] == null) {
                            this.readLabel((int)label, (Label[])labels).status |= 1;
                        }
                        v += 10;
                    }
                }
            } else if ("LocalVariableTypeTable".equals(attrName)) {
                varTypeTable = u + 8;
            } else if ("LineNumberTable".equals(attrName)) {
                if ((context.flags & 2) == 0) {
                    v = u;
                    for (j = this.readUnsignedShort(u + 8); j > 0; --j) {
                        label = this.readUnsignedShort(v + 10);
                        if (labels[label] == null) {
                            this.readLabel((int)label, (Label[])labels).status |= 1;
                        }
                        Label l = labels[label];
                        while (l.line > 0) {
                            if (l.next == null) {
                                l.next = new Label();
                            }
                            l = l.next;
                        }
                        l.line = this.readUnsignedShort(v + 12);
                        v += 4;
                    }
                }
            } else if ("RuntimeVisibleTypeAnnotations".equals(attrName)) {
                tanns = this.readTypeAnnotations(mv, context, u + 8, true);
                ntoff = tanns.length == 0 || this.readByte(tanns[0]) < 67 ? -1 : this.readUnsignedShort(tanns[0] + 1);
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attrName)) {
                itanns = this.readTypeAnnotations(mv, context, u + 8, false);
                nitoff = itanns.length == 0 || this.readByte(itanns[0]) < 67 ? -1 : this.readUnsignedShort(itanns[0] + 1);
            } else if ("StackMapTable".equals(attrName)) {
                if ((context.flags & 4) == 0) {
                    stackMap = u + 10;
                    stackMapSize = this.readInt(u + 4);
                    frameCount = this.readUnsignedShort(u + 8);
                }
            } else if ("StackMap".equals(attrName)) {
                if ((context.flags & 4) == 0) {
                    zip = false;
                    stackMap = u + 10;
                    stackMapSize = this.readInt(u + 4);
                    frameCount = this.readUnsignedShort(u + 8);
                }
            } else {
                for (j = 0; j < context.attrs.length; ++j) {
                    Attribute attr;
                    if (!context.attrs[j].type.equals(attrName) || (attr = context.attrs[j].read(this, u + 8, this.readInt(u + 4), c, codeStart - 8, labels)) == null) continue;
                    attr.next = attributes;
                    attributes = attr;
                }
            }
            u += 6 + this.readInt(u + 4);
        }
        u += 2;
        if (stackMap != 0) {
            frame = context;
            frame.offset = -1;
            frame.mode = 0;
            frame.localCount = 0;
            frame.localDiff = 0;
            frame.stackCount = 0;
            frame.local = new Object[maxLocals];
            frame.stack = new Object[maxStack];
            if (unzip) {
                this.getImplicitFrame(context);
            }
            for (i = stackMap; i < stackMap + stackMapSize - 2; ++i) {
                int v;
                if (b[i] != 8 || (v = this.readUnsignedShort(i + 1)) < 0 || v >= codeLength || (b[codeStart + v] & 0xFF) != 187) continue;
                this.readLabel(v, labels);
            }
        }
        if ((context.flags & 0x100) != 0) {
            mv.visitFrame(-1, maxLocals, null, 0, null);
        }
        int opcodeDelta = (context.flags & 0x100) == 0 ? -33 : 0;
        u = codeStart;
        while (u < codeEnd) {
            int offset = u - codeStart;
            Label l = labels[offset];
            if (l != null) {
                Label next = l.next;
                l.next = null;
                mv.visitLabel(l);
                if ((context.flags & 2) == 0 && l.line > 0) {
                    mv.visitLineNumber(l.line, l);
                    while (next != null) {
                        mv.visitLineNumber(next.line, l);
                        next = next.next;
                    }
                }
            }
            while (frame != null && (frame.offset == offset || frame.offset == -1)) {
                if (frame.offset != -1) {
                    if (!zip || unzip) {
                        mv.visitFrame(-1, frame.localCount, frame.local, frame.stackCount, frame.stack);
                    } else {
                        mv.visitFrame(frame.mode, frame.localDiff, frame.local, frame.stackCount, frame.stack);
                    }
                }
                if (frameCount > 0) {
                    stackMap = this.readFrame(stackMap, zip, unzip, frame);
                    --frameCount;
                    continue;
                }
                frame = null;
            }
            int opcode = b[u] & 0xFF;
            switch (ClassWriter.TYPE[opcode]) {
                case 0: {
                    mv.visitInsn(opcode);
                    ++u;
                    break;
                }
                case 4: {
                    if (opcode > 54) {
                        mv.visitVarInsn(54 + ((opcode -= 59) >> 2), opcode & 3);
                    } else {
                        mv.visitVarInsn(21 + ((opcode -= 26) >> 2), opcode & 3);
                    }
                    ++u;
                    break;
                }
                case 9: {
                    mv.visitJumpInsn(opcode, labels[offset + this.readShort(u + 1)]);
                    u += 3;
                    break;
                }
                case 10: {
                    mv.visitJumpInsn(opcode + opcodeDelta, labels[offset + this.readInt(u + 1)]);
                    u += 5;
                    break;
                }
                case 18: {
                    opcode = opcode < 218 ? opcode - 49 : opcode - 20;
                    Label target = labels[offset + this.readUnsignedShort(u + 1)];
                    if (opcode == 167 || opcode == 168) {
                        mv.visitJumpInsn(opcode + 33, target);
                    } else {
                        opcode = opcode <= 166 ? (opcode + 1 ^ 1) - 1 : opcode ^ 1;
                        Label endif = new Label();
                        mv.visitJumpInsn(opcode, endif);
                        mv.visitJumpInsn(200, target);
                        mv.visitLabel(endif);
                        if (stackMap != 0 && (frame == null || frame.offset != offset + 3)) {
                            mv.visitFrame(256, 0, null, 0, null);
                        }
                    }
                    u += 3;
                    break;
                }
                case 17: {
                    opcode = b[u + 1] & 0xFF;
                    if (opcode == 132) {
                        mv.visitIincInsn(this.readUnsignedShort(u + 2), this.readShort(u + 4));
                        u += 6;
                        break;
                    }
                    mv.visitVarInsn(opcode, this.readUnsignedShort(u + 2));
                    u += 4;
                    break;
                }
                case 14: {
                    u = u + 4 - (offset & 3);
                    int label = offset + this.readInt(u);
                    int min = this.readInt(u + 4);
                    int max = this.readInt(u + 8);
                    Label[] table = new Label[max - min + 1];
                    u += 12;
                    for (int i4 = 0; i4 < table.length; ++i4) {
                        table[i4] = labels[offset + this.readInt(u)];
                        u += 4;
                    }
                    mv.visitTableSwitchInsn(min, max, labels[label], table);
                    break;
                }
                case 15: {
                    u = u + 4 - (offset & 3);
                    int label = offset + this.readInt(u);
                    int len = this.readInt(u + 4);
                    int[] keys = new int[len];
                    Label[] values = new Label[len];
                    u += 8;
                    for (int i5 = 0; i5 < len; ++i5) {
                        keys[i5] = this.readInt(u);
                        values[i5] = labels[offset + this.readInt(u + 4)];
                        u += 8;
                    }
                    mv.visitLookupSwitchInsn(labels[label], keys, values);
                    break;
                }
                case 3: {
                    mv.visitVarInsn(opcode, b[u + 1] & 0xFF);
                    u += 2;
                    break;
                }
                case 1: {
                    mv.visitIntInsn(opcode, b[u + 1]);
                    u += 2;
                    break;
                }
                case 2: {
                    mv.visitIntInsn(opcode, this.readShort(u + 1));
                    u += 3;
                    break;
                }
                case 11: {
                    mv.visitLdcInsn(this.readConst(b[u + 1] & 0xFF, c));
                    u += 2;
                    break;
                }
                case 12: {
                    mv.visitLdcInsn(this.readConst(this.readUnsignedShort(u + 1), c));
                    u += 3;
                    break;
                }
                case 6: 
                case 7: {
                    int cpIndex = this.items[this.readUnsignedShort(u + 1)];
                    boolean itf = b[cpIndex - 1] == 11;
                    String iowner = this.readClass(cpIndex, c);
                    cpIndex = this.items[this.readUnsignedShort(cpIndex + 2)];
                    String iname = this.readUTF8(cpIndex, c);
                    String idesc = this.readUTF8(cpIndex + 2, c);
                    if (opcode < 182) {
                        mv.visitFieldInsn(opcode, iowner, iname, idesc);
                    } else {
                        mv.visitMethodInsn(opcode, iowner, iname, idesc, itf);
                    }
                    if (opcode == 185) {
                        u += 5;
                        break;
                    }
                    u += 3;
                    break;
                }
                case 8: {
                    int cpIndex = this.items[this.readUnsignedShort(u + 1)];
                    int bsmIndex = context.bootstrapMethods[this.readUnsignedShort(cpIndex)];
                    Handle bsm = (Handle)this.readConst(this.readUnsignedShort(bsmIndex), c);
                    int bsmArgCount = this.readUnsignedShort(bsmIndex + 2);
                    Object[] bsmArgs = new Object[bsmArgCount];
                    bsmIndex += 4;
                    for (int i6 = 0; i6 < bsmArgCount; ++i6) {
                        bsmArgs[i6] = this.readConst(this.readUnsignedShort(bsmIndex), c);
                        bsmIndex += 2;
                    }
                    cpIndex = this.items[this.readUnsignedShort(cpIndex + 2)];
                    String iname = this.readUTF8(cpIndex, c);
                    String idesc = this.readUTF8(cpIndex + 2, c);
                    mv.visitInvokeDynamicInsn(iname, idesc, bsm, bsmArgs);
                    u += 5;
                    break;
                }
                case 5: {
                    mv.visitTypeInsn(opcode, this.readClass(u + 1, c));
                    u += 3;
                    break;
                }
                case 13: {
                    mv.visitIincInsn(b[u + 1] & 0xFF, b[u + 2]);
                    u += 3;
                    break;
                }
                default: {
                    mv.visitMultiANewArrayInsn(this.readClass(u + 1, c), b[u + 3] & 0xFF);
                    u += 4;
                }
            }
            while (tanns != null && tann < tanns.length && ntoff <= offset) {
                if (ntoff == offset) {
                    int v = this.readAnnotationTarget(context, tanns[tann]);
                    this.readAnnotationValues(v + 2, c, true, mv.visitInsnAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), true));
                }
                ntoff = ++tann >= tanns.length || this.readByte(tanns[tann]) < 67 ? -1 : this.readUnsignedShort(tanns[tann] + 1);
            }
            while (itanns != null && itann < itanns.length && nitoff <= offset) {
                if (nitoff == offset) {
                    int v = this.readAnnotationTarget(context, itanns[itann]);
                    this.readAnnotationValues(v + 2, c, true, mv.visitInsnAnnotation(context.typeRef, context.typePath, this.readUTF8(v, c), false));
                }
                nitoff = ++itann >= itanns.length || this.readByte(itanns[itann]) < 67 ? -1 : this.readUnsignedShort(itanns[itann] + 1);
            }
        }
        if (labels[codeLength] != null) {
            mv.visitLabel(labels[codeLength]);
        }
        if ((context.flags & 2) == 0 && varTable != 0) {
            int[] typeTable = null;
            if (varTypeTable != 0) {
                u = varTypeTable + 2;
                typeTable = new int[this.readUnsignedShort(varTypeTable) * 3];
                int i7 = typeTable.length;
                while (i7 > 0) {
                    typeTable[--i7] = u + 6;
                    typeTable[--i7] = this.readUnsignedShort(u + 8);
                    typeTable[--i7] = this.readUnsignedShort(u);
                    u += 10;
                }
            }
            u = varTable + 2;
            for (int i8 = this.readUnsignedShort(varTable); i8 > 0; --i8) {
                int start = this.readUnsignedShort(u);
                int length = this.readUnsignedShort(u + 2);
                int index = this.readUnsignedShort(u + 8);
                String vsignature = null;
                if (typeTable != null) {
                    for (int j = 0; j < typeTable.length; j += 3) {
                        if (typeTable[j] != start || typeTable[j + 1] != index) continue;
                        vsignature = this.readUTF8(typeTable[j + 2], c);
                        break;
                    }
                }
                mv.visitLocalVariable(this.readUTF8(u + 4, c), this.readUTF8(u + 6, c), vsignature, labels[start], labels[start + length], index);
                u += 10;
            }
        }
        if (tanns != null) {
            for (int i9 = 0; i9 < tanns.length; ++i9) {
                if (this.readByte(tanns[i9]) >> 1 != 32) continue;
                int v = this.readAnnotationTarget(context, tanns[i9]);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitLocalVariableAnnotation(context.typeRef, context.typePath, context.start, context.end, context.index, this.readUTF8(v, c), true));
            }
        }
        if (itanns != null) {
            for (int i10 = 0; i10 < itanns.length; ++i10) {
                if (this.readByte(itanns[i10]) >> 1 != 32) continue;
                int v = this.readAnnotationTarget(context, itanns[i10]);
                v = this.readAnnotationValues(v + 2, c, true, mv.visitLocalVariableAnnotation(context.typeRef, context.typePath, context.start, context.end, context.index, this.readUTF8(v, c), false));
            }
        }
        while (attributes != null) {
            Attribute attr = attributes.next;
            attributes.next = null;
            mv.visitAttribute(attributes);
            attributes = attr;
        }
        mv.visitMaxs(maxStack, maxLocals);
    }

    private int[] readTypeAnnotations(MethodVisitor mv, Context context, int u, boolean visible) {
        char[] c = context.buffer;
        int[] offsets = new int[this.readUnsignedShort(u)];
        u += 2;
        for (int i = 0; i < offsets.length; ++i) {
            offsets[i] = u;
            int target = this.readInt(u);
            switch (target >>> 24) {
                case 0: 
                case 1: 
                case 22: {
                    u += 2;
                    break;
                }
                case 19: 
                case 20: 
                case 21: {
                    ++u;
                    break;
                }
                case 64: 
                case 65: {
                    for (int j = this.readUnsignedShort(u + 1); j > 0; --j) {
                        int start = this.readUnsignedShort(u + 3);
                        int length = this.readUnsignedShort(u + 5);
                        this.readLabel(start, context.labels);
                        this.readLabel(start + length, context.labels);
                        u += 6;
                    }
                    u += 3;
                    break;
                }
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: {
                    u += 4;
                    break;
                }
                default: {
                    u += 3;
                }
            }
            int pathLength = this.readByte(u);
            if (target >>> 24 == 66) {
                TypePath path = pathLength == 0 ? null : new TypePath(this.b, u);
                u += 1 + 2 * pathLength;
                u = this.readAnnotationValues(u + 2, c, true, mv.visitTryCatchAnnotation(target, path, this.readUTF8(u, c), visible));
                continue;
            }
            u = this.readAnnotationValues(u + 3 + 2 * pathLength, c, true, null);
        }
        return offsets;
    }

    private int readAnnotationTarget(Context context, int u) {
        int target = this.readInt(u);
        switch (target >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                target &= 0xFFFF0000;
                u += 2;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                target &= 0xFF000000;
                ++u;
                break;
            }
            case 64: 
            case 65: {
                target &= 0xFF000000;
                int n = this.readUnsignedShort(u + 1);
                context.start = new Label[n];
                context.end = new Label[n];
                context.index = new int[n];
                u += 3;
                for (int i = 0; i < n; ++i) {
                    int start = this.readUnsignedShort(u);
                    int length = this.readUnsignedShort(u + 2);
                    context.start[i] = this.readLabel(start, context.labels);
                    context.end[i] = this.readLabel(start + length, context.labels);
                    context.index[i] = this.readUnsignedShort(u + 4);
                    u += 6;
                }
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                target &= 0xFF0000FF;
                u += 4;
                break;
            }
            default: {
                target &= target >>> 24 < 67 ? -256 : -16777216;
                u += 3;
            }
        }
        int pathLength = this.readByte(u);
        context.typeRef = target;
        context.typePath = pathLength == 0 ? null : new TypePath(this.b, u);
        return u + 1 + 2 * pathLength;
    }

    private void readParameterAnnotations(MethodVisitor mv, Context context, int v, boolean visible) {
        AnnotationVisitor av;
        int i;
        int n = this.b[v++] & 0xFF;
        int synthetics = Type.getArgumentTypes(context.desc).length - n;
        for (i = 0; i < synthetics; ++i) {
            av = mv.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
            if (av == null) continue;
            av.visitEnd();
        }
        char[] c = context.buffer;
        while (i < n + synthetics) {
            int j = this.readUnsignedShort(v);
            v += 2;
            while (j > 0) {
                av = mv.visitParameterAnnotation(i, this.readUTF8(v, c), visible);
                v = this.readAnnotationValues(v + 2, c, true, av);
                --j;
            }
            ++i;
        }
    }

    private int readAnnotationValues(int v, char[] buf, boolean named, AnnotationVisitor av) {
        int i = this.readUnsignedShort(v);
        v += 2;
        if (named) {
            while (i > 0) {
                v = this.readAnnotationValue(v + 2, buf, this.readUTF8(v, buf), av);
                --i;
            }
        } else {
            while (i > 0) {
                v = this.readAnnotationValue(v, buf, null, av);
                --i;
            }
        }
        if (av != null) {
            av.visitEnd();
        }
        return v;
    }

    private int readAnnotationValue(int v, char[] buf, String name, AnnotationVisitor av) {
        if (av == null) {
            switch (this.b[v] & 0xFF) {
                case 101: {
                    return v + 5;
                }
                case 64: {
                    return this.readAnnotationValues(v + 3, buf, true, null);
                }
                case 91: {
                    return this.readAnnotationValues(v + 1, buf, false, null);
                }
            }
            return v + 3;
        }
        block5 : switch (this.b[v++] & 0xFF) {
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                av.visit(name, this.readConst(this.readUnsignedShort(v), buf));
                v += 2;
                break;
            }
            case 66: {
                av.visit(name, (byte)this.readInt(this.items[this.readUnsignedShort(v)]));
                v += 2;
                break;
            }
            case 90: {
                av.visit(name, this.readInt(this.items[this.readUnsignedShort(v)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                v += 2;
                break;
            }
            case 83: {
                av.visit(name, (short)this.readInt(this.items[this.readUnsignedShort(v)]));
                v += 2;
                break;
            }
            case 67: {
                av.visit(name, Character.valueOf((char)this.readInt(this.items[this.readUnsignedShort(v)])));
                v += 2;
                break;
            }
            case 115: {
                av.visit(name, this.readUTF8(v, buf));
                v += 2;
                break;
            }
            case 101: {
                av.visitEnum(name, this.readUTF8(v, buf), this.readUTF8(v + 2, buf));
                v += 4;
                break;
            }
            case 99: {
                av.visit(name, Type.getType(this.readUTF8(v, buf)));
                v += 2;
                break;
            }
            case 64: {
                v = this.readAnnotationValues(v + 2, buf, true, av.visitAnnotation(name, this.readUTF8(v, buf)));
                break;
            }
            case 91: {
                int size = this.readUnsignedShort(v);
                v += 2;
                if (size == 0) {
                    return this.readAnnotationValues(v - 2, buf, false, av.visitArray(name));
                }
                switch (this.b[v++] & 0xFF) {
                    case 66: {
                        byte[] bv = new byte[size];
                        for (int i = 0; i < size; ++i) {
                            bv[i] = (byte)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                        }
                        av.visit(name, bv);
                        --v;
                        break block5;
                    }
                    case 90: {
                        boolean[] zv = new boolean[size];
                        for (int i = 0; i < size; ++i) {
                            zv[i] = this.readInt(this.items[this.readUnsignedShort(v)]) != 0;
                            v += 3;
                        }
                        av.visit(name, zv);
                        --v;
                        break block5;
                    }
                    case 83: {
                        short[] sv = new short[size];
                        for (int i = 0; i < size; ++i) {
                            sv[i] = (short)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                        }
                        av.visit(name, sv);
                        --v;
                        break block5;
                    }
                    case 67: {
                        char[] cv = new char[size];
                        for (int i = 0; i < size; ++i) {
                            cv[i] = (char)this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                        }
                        av.visit(name, cv);
                        --v;
                        break block5;
                    }
                    case 73: {
                        int[] iv = new int[size];
                        for (int i = 0; i < size; ++i) {
                            iv[i] = this.readInt(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                        }
                        av.visit(name, iv);
                        --v;
                        break block5;
                    }
                    case 74: {
                        long[] lv = new long[size];
                        for (int i = 0; i < size; ++i) {
                            lv[i] = this.readLong(this.items[this.readUnsignedShort(v)]);
                            v += 3;
                        }
                        av.visit(name, lv);
                        --v;
                        break block5;
                    }
                    case 70: {
                        float[] fv = new float[size];
                        for (int i = 0; i < size; ++i) {
                            fv[i] = Float.intBitsToFloat(this.readInt(this.items[this.readUnsignedShort(v)]));
                            v += 3;
                        }
                        av.visit(name, fv);
                        --v;
                        break block5;
                    }
                    case 68: {
                        double[] dv = new double[size];
                        for (int i = 0; i < size; ++i) {
                            dv[i] = Double.longBitsToDouble(this.readLong(this.items[this.readUnsignedShort(v)]));
                            v += 3;
                        }
                        av.visit(name, dv);
                        --v;
                        break block5;
                    }
                }
                v = this.readAnnotationValues(v - 3, buf, false, av.visitArray(name));
            }
        }
        return v;
    }

    private void getImplicitFrame(Context frame) {
        String desc = frame.desc;
        Object[] locals = frame.local;
        int local = 0;
        if ((frame.access & 8) == 0) {
            locals[local++] = "<init>".equals(frame.name) ? Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, frame.buffer);
        }
        int i = 1;
        block8: while (true) {
            int j = i;
            switch (desc.charAt(i++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    locals[local++] = Opcodes.INTEGER;
                    continue block8;
                }
                case 'F': {
                    locals[local++] = Opcodes.FLOAT;
                    continue block8;
                }
                case 'J': {
                    locals[local++] = Opcodes.LONG;
                    continue block8;
                }
                case 'D': {
                    locals[local++] = Opcodes.DOUBLE;
                    continue block8;
                }
                case '[': {
                    while (desc.charAt(i) == '[') {
                        ++i;
                    }
                    if (desc.charAt(i) == 'L') {
                        ++i;
                        while (desc.charAt(i) != ';') {
                            ++i;
                        }
                    }
                    locals[local++] = desc.substring(j, ++i);
                    continue block8;
                }
                case 'L': {
                    while (desc.charAt(i) != ';') {
                        ++i;
                    }
                    locals[local++] = desc.substring(j + 1, i++);
                    continue block8;
                }
            }
            break;
        }
        frame.localCount = local;
    }

    private int readFrame(int stackMap, boolean zip, boolean unzip, Context frame) {
        int delta;
        int tag;
        char[] c = frame.buffer;
        Label[] labels = frame.labels;
        if (zip) {
            tag = this.b[stackMap++] & 0xFF;
        } else {
            tag = 255;
            frame.offset = -1;
        }
        frame.localDiff = 0;
        if (tag < 64) {
            delta = tag;
            frame.mode = 3;
            frame.stackCount = 0;
        } else if (tag < 128) {
            delta = tag - 64;
            stackMap = this.readFrameType(frame.stack, 0, stackMap, c, labels);
            frame.mode = 4;
            frame.stackCount = 1;
        } else {
            delta = this.readUnsignedShort(stackMap);
            stackMap += 2;
            if (tag == 247) {
                stackMap = this.readFrameType(frame.stack, 0, stackMap, c, labels);
                frame.mode = 4;
                frame.stackCount = 1;
            } else if (tag >= 248 && tag < 251) {
                frame.mode = 2;
                frame.localDiff = 251 - tag;
                frame.localCount -= frame.localDiff;
                frame.stackCount = 0;
            } else if (tag == 251) {
                frame.mode = 3;
                frame.stackCount = 0;
            } else if (tag < 255) {
                int local = unzip ? frame.localCount : 0;
                for (int i = tag - 251; i > 0; --i) {
                    stackMap = this.readFrameType(frame.local, local++, stackMap, c, labels);
                }
                frame.mode = 1;
                frame.localDiff = tag - 251;
                frame.localCount += frame.localDiff;
                frame.stackCount = 0;
            } else {
                frame.mode = 0;
                int n = this.readUnsignedShort(stackMap);
                stackMap += 2;
                frame.localDiff = n;
                frame.localCount = n;
                int local = 0;
                while (n > 0) {
                    stackMap = this.readFrameType(frame.local, local++, stackMap, c, labels);
                    --n;
                }
                n = this.readUnsignedShort(stackMap);
                stackMap += 2;
                frame.stackCount = n;
                int stack = 0;
                while (n > 0) {
                    stackMap = this.readFrameType(frame.stack, stack++, stackMap, c, labels);
                    --n;
                }
            }
        }
        frame.offset += delta + 1;
        this.readLabel(frame.offset, labels);
        return stackMap;
    }

    private int readFrameType(Object[] frame, int index, int v, char[] buf, Label[] labels) {
        int type = this.b[v++] & 0xFF;
        switch (type) {
            case 0: {
                frame[index] = Opcodes.TOP;
                break;
            }
            case 1: {
                frame[index] = Opcodes.INTEGER;
                break;
            }
            case 2: {
                frame[index] = Opcodes.FLOAT;
                break;
            }
            case 3: {
                frame[index] = Opcodes.DOUBLE;
                break;
            }
            case 4: {
                frame[index] = Opcodes.LONG;
                break;
            }
            case 5: {
                frame[index] = Opcodes.NULL;
                break;
            }
            case 6: {
                frame[index] = Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                frame[index] = this.readClass(v, buf);
                v += 2;
                break;
            }
            default: {
                frame[index] = this.readLabel(this.readUnsignedShort(v), labels);
                v += 2;
            }
        }
        return v;
    }

    protected Label readLabel(int offset, Label[] labels) {
        if (labels[offset] == null) {
            labels[offset] = new Label();
        }
        return labels[offset];
    }

    private int getAttributes() {
        int j;
        int i;
        int u = this.header + 8 + this.readUnsignedShort(this.header + 6) * 2;
        for (i = this.readUnsignedShort(u); i > 0; --i) {
            for (j = this.readUnsignedShort(u + 8); j > 0; --j) {
                u += 6 + this.readInt(u + 12);
            }
            u += 8;
        }
        for (i = this.readUnsignedShort(u += 2); i > 0; --i) {
            for (j = this.readUnsignedShort(u + 8); j > 0; --j) {
                u += 6 + this.readInt(u + 12);
            }
            u += 8;
        }
        return u + 2;
    }

    private Attribute readAttribute(Attribute[] attrs, String type, int off, int len, char[] buf, int codeOff, Label[] labels) {
        for (int i = 0; i < attrs.length; ++i) {
            if (!attrs[i].type.equals(type)) continue;
            return attrs[i].read(this, off, len, buf, codeOff, labels);
        }
        return new Attribute(type).read(this, off, len, null, -1, null);
    }

    public int getItemCount() {
        return this.items.length;
    }

    public int getItem(int item) {
        return this.items[item];
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int readByte(int index) {
        return this.b[index] & 0xFF;
    }

    public int readUnsignedShort(int index) {
        byte[] b = this.b;
        return (b[index] & 0xFF) << 8 | b[index + 1] & 0xFF;
    }

    public short readShort(int index) {
        byte[] b = this.b;
        return (short)((b[index] & 0xFF) << 8 | b[index + 1] & 0xFF);
    }

    public int readInt(int index) {
        byte[] b = this.b;
        return (b[index] & 0xFF) << 24 | (b[index + 1] & 0xFF) << 16 | (b[index + 2] & 0xFF) << 8 | b[index + 3] & 0xFF;
    }

    public long readLong(int index) {
        long l1 = this.readInt(index);
        long l0 = (long)this.readInt(index + 4) & 0xFFFFFFFFL;
        return l1 << 32 | l0;
    }

    public String readUTF8(int index, char[] buf) {
        int item = this.readUnsignedShort(index);
        if (index == 0 || item == 0) {
            return null;
        }
        String s = this.strings[item];
        if (s != null) {
            return s;
        }
        index = this.items[item];
        this.strings[item] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
        return this.strings[item];
    }

    private String readUTF(int index, int utfLen, char[] buf) {
        int endIndex = index + utfLen;
        byte[] b = this.b;
        int strLen = 0;
        int st = 0;
        int cc = 0;
        while (index < endIndex) {
            int c = b[index++];
            switch (st) {
                case 0: {
                    if ((c &= 0xFF) < 128) {
                        buf[strLen++] = (char)c;
                        break;
                    }
                    if (c < 224 && c > 191) {
                        cc = (char)(c & 0x1F);
                        st = 1;
                        break;
                    }
                    cc = (char)(c & 0xF);
                    st = 2;
                    break;
                }
                case 1: {
                    buf[strLen++] = (char)(cc << 6 | c & 0x3F);
                    st = 0;
                    break;
                }
                case 2: {
                    cc = (char)(cc << 6 | c & 0x3F);
                    st = 1;
                }
            }
        }
        return new String(buf, 0, strLen);
    }

    public String readClass(int index, char[] buf) {
        return this.readUTF8(this.items[this.readUnsignedShort(index)], buf);
    }

    public Object readConst(int item, char[] buf) {
        int index = this.items[item];
        switch (this.b[index - 1]) {
            case 3: {
                return this.readInt(index);
            }
            case 4: {
                return Float.valueOf(Float.intBitsToFloat(this.readInt(index)));
            }
            case 5: {
                return this.readLong(index);
            }
            case 6: {
                return Double.longBitsToDouble(this.readLong(index));
            }
            case 7: {
                return Type.getObjectType(this.readUTF8(index, buf));
            }
            case 8: {
                return this.readUTF8(index, buf);
            }
            case 16: {
                return Type.getMethodType(this.readUTF8(index, buf));
            }
        }
        int tag = this.readByte(index);
        int[] items = this.items;
        int cpIndex = items[this.readUnsignedShort(index + 1)];
        boolean itf = this.b[cpIndex - 1] == 11;
        String owner = this.readClass(cpIndex, buf);
        cpIndex = items[this.readUnsignedShort(cpIndex + 2)];
        String name = this.readUTF8(cpIndex, buf);
        String desc = this.readUTF8(cpIndex + 2, buf);
        return new Handle(tag, owner, name, desc, itf);
    }
}

