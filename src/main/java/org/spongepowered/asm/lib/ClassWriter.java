/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.AnnotationWriter;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.FieldWriter;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.MethodWriter;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

public class ClassWriter
extends ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final int ACC_SYNTHETIC_ATTRIBUTE = 262144;
    static final int TO_ACC_SYNTHETIC = 64;
    static final int NOARG_INSN = 0;
    static final int SBYTE_INSN = 1;
    static final int SHORT_INSN = 2;
    static final int VAR_INSN = 3;
    static final int IMPLVAR_INSN = 4;
    static final int TYPE_INSN = 5;
    static final int FIELDORMETH_INSN = 6;
    static final int ITFMETH_INSN = 7;
    static final int INDYMETH_INSN = 8;
    static final int LABEL_INSN = 9;
    static final int LABELW_INSN = 10;
    static final int LDC_INSN = 11;
    static final int LDCW_INSN = 12;
    static final int IINC_INSN = 13;
    static final int TABL_INSN = 14;
    static final int LOOK_INSN = 15;
    static final int MANA_INSN = 16;
    static final int WIDE_INSN = 17;
    static final int ASM_LABEL_INSN = 18;
    static final int F_INSERT = 256;
    static final byte[] TYPE;
    static final int CLASS = 7;
    static final int FIELD = 9;
    static final int METH = 10;
    static final int IMETH = 11;
    static final int STR = 8;
    static final int INT = 3;
    static final int FLOAT = 4;
    static final int LONG = 5;
    static final int DOUBLE = 6;
    static final int NAME_TYPE = 12;
    static final int UTF8 = 1;
    static final int MTYPE = 16;
    static final int HANDLE = 15;
    static final int INDY = 18;
    static final int HANDLE_BASE = 20;
    static final int TYPE_NORMAL = 30;
    static final int TYPE_UNINIT = 31;
    static final int TYPE_MERGED = 32;
    static final int BSM = 33;
    ClassReader cr;
    int version;
    int index = 1;
    final ByteVector pool = new ByteVector();
    Item[] items = new Item[256];
    int threshold = (int)(0.75 * (double)this.items.length);
    final Item key = new Item();
    final Item key2 = new Item();
    final Item key3 = new Item();
    final Item key4 = new Item();
    Item[] typeTable;
    private short typeCount;
    private int access;
    private int name;
    String thisName;
    private int signature;
    private int superName;
    private int interfaceCount;
    private int[] interfaces;
    private int sourceFile;
    private ByteVector sourceDebug;
    private int enclosingMethodOwner;
    private int enclosingMethod;
    private AnnotationWriter anns;
    private AnnotationWriter ianns;
    private AnnotationWriter tanns;
    private AnnotationWriter itanns;
    private Attribute attrs;
    private int innerClassesCount;
    private ByteVector innerClasses;
    int bootstrapMethodsCount;
    ByteVector bootstrapMethods;
    FieldWriter firstField;
    FieldWriter lastField;
    MethodWriter firstMethod;
    MethodWriter lastMethod;
    private int compute;
    boolean hasAsmInsns;

    public ClassWriter(int flags) {
        super(327680);
        this.compute = (flags & 2) != 0 ? 0 : ((flags & 1) != 0 ? 2 : 3);
    }

    public ClassWriter(ClassReader classReader, int flags) {
        this(flags);
        classReader.copyPool(this);
        this.cr = classReader;
    }

    public final void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = this.newClass(name);
        this.thisName = name;
        if (signature != null) {
            this.signature = this.newUTF8(signature);
        }
        int n = this.superName = superName == null ? 0 : this.newClass(superName);
        if (interfaces != null && interfaces.length > 0) {
            this.interfaceCount = interfaces.length;
            this.interfaces = new int[this.interfaceCount];
            for (int i = 0; i < this.interfaceCount; ++i) {
                this.interfaces[i] = this.newClass(interfaces[i]);
            }
        }
    }

    public final void visitSource(String file, String debug) {
        if (file != null) {
            this.sourceFile = this.newUTF8(file);
        }
        if (debug != null) {
            this.sourceDebug = new ByteVector().encodeUTF8(debug, 0, Integer.MAX_VALUE);
        }
    }

    public final void visitOuterClass(String owner, String name, String desc) {
        this.enclosingMethodOwner = this.newClass(owner);
        if (name != null && desc != null) {
            this.enclosingMethod = this.newNameType(name, desc);
        }
    }

    public final AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        bv.putShort(this.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv, 2);
        if (visible) {
            aw.next = this.anns;
            this.anns = aw;
        } else {
            aw.next = this.ianns;
            this.ianns = aw;
        }
        return aw;
    }

    public final AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        bv.putShort(this.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.tanns;
            this.tanns = aw;
        } else {
            aw.next = this.itanns;
            this.itanns = aw;
        }
        return aw;
    }

    public final void visitAttribute(Attribute attr) {
        attr.next = this.attrs;
        this.attrs = attr;
    }

    public final void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (this.innerClasses == null) {
            this.innerClasses = new ByteVector();
        }
        Item nameItem = this.newClassItem(name);
        if (nameItem.intVal == 0) {
            ++this.innerClassesCount;
            this.innerClasses.putShort(nameItem.index);
            this.innerClasses.putShort(outerName == null ? 0 : this.newClass(outerName));
            this.innerClasses.putShort(innerName == null ? 0 : this.newUTF8(innerName));
            this.innerClasses.putShort(access);
            nameItem.intVal = this.innerClassesCount;
        }
    }

    public final FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new FieldWriter(this, access, name, desc, signature, value);
    }

    public final MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodWriter(this, access, name, desc, signature, exceptions, this.compute);
    }

    public final void visitEnd() {
    }

    public byte[] toByteArray() {
        if (this.index > 65535) {
            throw new RuntimeException("Class file too large!");
        }
        int size = 24 + 2 * this.interfaceCount;
        int nbFields = 0;
        FieldWriter fb = this.firstField;
        while (fb != null) {
            ++nbFields;
            size += fb.getSize();
            fb = (FieldWriter)fb.fv;
        }
        int nbMethods = 0;
        MethodWriter mb = this.firstMethod;
        while (mb != null) {
            ++nbMethods;
            size += mb.getSize();
            mb = (MethodWriter)mb.mv;
        }
        int attributeCount = 0;
        if (this.bootstrapMethods != null) {
            ++attributeCount;
            size += 8 + this.bootstrapMethods.length;
            this.newUTF8("BootstrapMethods");
        }
        if (this.signature != 0) {
            ++attributeCount;
            size += 8;
            this.newUTF8("Signature");
        }
        if (this.sourceFile != 0) {
            ++attributeCount;
            size += 8;
            this.newUTF8("SourceFile");
        }
        if (this.sourceDebug != null) {
            ++attributeCount;
            size += this.sourceDebug.length + 6;
            this.newUTF8("SourceDebugExtension");
        }
        if (this.enclosingMethodOwner != 0) {
            ++attributeCount;
            size += 10;
            this.newUTF8("EnclosingMethod");
        }
        if ((this.access & 0x20000) != 0) {
            ++attributeCount;
            size += 6;
            this.newUTF8("Deprecated");
        }
        if ((this.access & 0x1000) != 0 && ((this.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            ++attributeCount;
            size += 6;
            this.newUTF8("Synthetic");
        }
        if (this.innerClasses != null) {
            ++attributeCount;
            size += 8 + this.innerClasses.length;
            this.newUTF8("InnerClasses");
        }
        if (this.anns != null) {
            ++attributeCount;
            size += 8 + this.anns.getSize();
            this.newUTF8("RuntimeVisibleAnnotations");
        }
        if (this.ianns != null) {
            ++attributeCount;
            size += 8 + this.ianns.getSize();
            this.newUTF8("RuntimeInvisibleAnnotations");
        }
        if (this.tanns != null) {
            ++attributeCount;
            size += 8 + this.tanns.getSize();
            this.newUTF8("RuntimeVisibleTypeAnnotations");
        }
        if (this.itanns != null) {
            ++attributeCount;
            size += 8 + this.itanns.getSize();
            this.newUTF8("RuntimeInvisibleTypeAnnotations");
        }
        if (this.attrs != null) {
            attributeCount += this.attrs.getCount();
            size += this.attrs.getSize(this, null, 0, -1, -1);
        }
        ByteVector out = new ByteVector(size += this.pool.length);
        out.putInt(-889275714).putInt(this.version);
        out.putShort(this.index).putByteArray(this.pool.data, 0, this.pool.length);
        int mask = 0x60000 | (this.access & 0x40000) / 64;
        out.putShort(this.access & ~mask).putShort(this.name).putShort(this.superName);
        out.putShort(this.interfaceCount);
        for (int i = 0; i < this.interfaceCount; ++i) {
            out.putShort(this.interfaces[i]);
        }
        out.putShort(nbFields);
        fb = this.firstField;
        while (fb != null) {
            fb.put(out);
            fb = (FieldWriter)fb.fv;
        }
        out.putShort(nbMethods);
        mb = this.firstMethod;
        while (mb != null) {
            mb.put(out);
            mb = (MethodWriter)mb.mv;
        }
        out.putShort(attributeCount);
        if (this.bootstrapMethods != null) {
            out.putShort(this.newUTF8("BootstrapMethods"));
            out.putInt(this.bootstrapMethods.length + 2).putShort(this.bootstrapMethodsCount);
            out.putByteArray(this.bootstrapMethods.data, 0, this.bootstrapMethods.length);
        }
        if (this.signature != 0) {
            out.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.signature);
        }
        if (this.sourceFile != 0) {
            out.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.sourceFile);
        }
        if (this.sourceDebug != null) {
            int len = this.sourceDebug.length;
            out.putShort(this.newUTF8("SourceDebugExtension")).putInt(len);
            out.putByteArray(this.sourceDebug.data, 0, len);
        }
        if (this.enclosingMethodOwner != 0) {
            out.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
            out.putShort(this.enclosingMethodOwner).putShort(this.enclosingMethod);
        }
        if ((this.access & 0x20000) != 0) {
            out.putShort(this.newUTF8("Deprecated")).putInt(0);
        }
        if ((this.access & 0x1000) != 0 && ((this.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            out.putShort(this.newUTF8("Synthetic")).putInt(0);
        }
        if (this.innerClasses != null) {
            out.putShort(this.newUTF8("InnerClasses"));
            out.putInt(this.innerClasses.length + 2).putShort(this.innerClassesCount);
            out.putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
        }
        if (this.anns != null) {
            out.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
            this.anns.put(out);
        }
        if (this.ianns != null) {
            out.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
            this.ianns.put(out);
        }
        if (this.tanns != null) {
            out.putShort(this.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.tanns.put(out);
        }
        if (this.itanns != null) {
            out.putShort(this.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.itanns.put(out);
        }
        if (this.attrs != null) {
            this.attrs.put(this, null, 0, -1, -1, out);
        }
        if (this.hasAsmInsns) {
            this.anns = null;
            this.ianns = null;
            this.attrs = null;
            this.innerClassesCount = 0;
            this.innerClasses = null;
            this.firstField = null;
            this.lastField = null;
            this.firstMethod = null;
            this.lastMethod = null;
            this.compute = 1;
            this.hasAsmInsns = false;
            new ClassReader(out.data).accept(this, 264);
            return this.toByteArray();
        }
        return out.data;
    }

    Item newConstItem(Object cst) {
        if (cst instanceof Integer) {
            int val = (Integer)cst;
            return this.newInteger(val);
        }
        if (cst instanceof Byte) {
            int val = ((Byte)cst).intValue();
            return this.newInteger(val);
        }
        if (cst instanceof Character) {
            char val = ((Character)cst).charValue();
            return this.newInteger(val);
        }
        if (cst instanceof Short) {
            int val = ((Short)cst).intValue();
            return this.newInteger(val);
        }
        if (cst instanceof Boolean) {
            int val = (Boolean)cst != false ? 1 : 0;
            return this.newInteger(val);
        }
        if (cst instanceof Float) {
            float val = ((Float)cst).floatValue();
            return this.newFloat(val);
        }
        if (cst instanceof Long) {
            long val = (Long)cst;
            return this.newLong(val);
        }
        if (cst instanceof Double) {
            double val = (Double)cst;
            return this.newDouble(val);
        }
        if (cst instanceof String) {
            return this.newString((String)cst);
        }
        if (cst instanceof Type) {
            Type t = (Type)cst;
            int s = t.getSort();
            if (s == 10) {
                return this.newClassItem(t.getInternalName());
            }
            if (s == 11) {
                return this.newMethodTypeItem(t.getDescriptor());
            }
            return this.newClassItem(t.getDescriptor());
        }
        if (cst instanceof Handle) {
            Handle h = (Handle)cst;
            return this.newHandleItem(h.tag, h.owner, h.name, h.desc, h.itf);
        }
        throw new IllegalArgumentException("value " + cst);
    }

    public int newConst(Object cst) {
        return this.newConstItem((Object)cst).index;
    }

    public int newUTF8(String value) {
        this.key.set(1, value, null, null);
        Item result = this.get(this.key);
        if (result == null) {
            this.pool.putByte(1).putUTF8(value);
            result = new Item(this.index++, this.key);
            this.put(result);
        }
        return result.index;
    }

    Item newClassItem(String value) {
        this.key2.set(7, value, null, null);
        Item result = this.get(this.key2);
        if (result == null) {
            this.pool.put12(7, this.newUTF8(value));
            result = new Item(this.index++, this.key2);
            this.put(result);
        }
        return result;
    }

    public int newClass(String value) {
        return this.newClassItem((String)value).index;
    }

    Item newMethodTypeItem(String methodDesc) {
        this.key2.set(16, methodDesc, null, null);
        Item result = this.get(this.key2);
        if (result == null) {
            this.pool.put12(16, this.newUTF8(methodDesc));
            result = new Item(this.index++, this.key2);
            this.put(result);
        }
        return result;
    }

    public int newMethodType(String methodDesc) {
        return this.newMethodTypeItem((String)methodDesc).index;
    }

    Item newHandleItem(int tag, String owner, String name, String desc, boolean itf) {
        this.key4.set(20 + tag, owner, name, desc);
        Item result = this.get(this.key4);
        if (result == null) {
            if (tag <= 4) {
                this.put112(15, tag, this.newField(owner, name, desc));
            } else {
                this.put112(15, tag, this.newMethod(owner, name, desc, itf));
            }
            result = new Item(this.index++, this.key4);
            this.put(result);
        }
        return result;
    }

    @Deprecated
    public int newHandle(int tag, String owner, String name, String desc) {
        return this.newHandle(tag, owner, name, desc, tag == 9);
    }

    public int newHandle(int tag, String owner, String name, String desc, boolean itf) {
        return this.newHandleItem((int)tag, (String)owner, (String)name, (String)desc, (boolean)itf).index;
    }

    Item newInvokeDynamicItem(String name, String desc, Handle bsm, Object ... bsmArgs) {
        int bootstrapMethodIndex;
        ByteVector bootstrapMethods = this.bootstrapMethods;
        if (bootstrapMethods == null) {
            bootstrapMethods = this.bootstrapMethods = new ByteVector();
        }
        int position = bootstrapMethods.length;
        int hashCode = bsm.hashCode();
        bootstrapMethods.putShort(this.newHandle(bsm.tag, bsm.owner, bsm.name, bsm.desc, bsm.isInterface()));
        int argsLength = bsmArgs.length;
        bootstrapMethods.putShort(argsLength);
        for (int i = 0; i < argsLength; ++i) {
            Object bsmArg = bsmArgs[i];
            hashCode ^= bsmArg.hashCode();
            bootstrapMethods.putShort(this.newConst(bsmArg));
        }
        byte[] data = bootstrapMethods.data;
        int length = 2 + argsLength << 1;
        Item result = this.items[(hashCode &= Integer.MAX_VALUE) % this.items.length];
        block1: while (result != null) {
            if (result.type != 33 || result.hashCode != hashCode) {
                result = result.next;
                continue;
            }
            int resultPosition = result.intVal;
            for (int p = 0; p < length; ++p) {
                if (data[position + p] == data[resultPosition + p]) continue;
                result = result.next;
                continue block1;
            }
        }
        if (result != null) {
            bootstrapMethodIndex = result.index;
            bootstrapMethods.length = position;
        } else {
            bootstrapMethodIndex = this.bootstrapMethodsCount++;
            result = new Item(bootstrapMethodIndex);
            result.set(position, hashCode);
            this.put(result);
        }
        this.key3.set(name, desc, bootstrapMethodIndex);
        result = this.get(this.key3);
        if (result == null) {
            this.put122(18, bootstrapMethodIndex, this.newNameType(name, desc));
            result = new Item(this.index++, this.key3);
            this.put(result);
        }
        return result;
    }

    public int newInvokeDynamic(String name, String desc, Handle bsm, Object ... bsmArgs) {
        return this.newInvokeDynamicItem((String)name, (String)desc, (Handle)bsm, (Object[])bsmArgs).index;
    }

    Item newFieldItem(String owner, String name, String desc) {
        this.key3.set(9, owner, name, desc);
        Item result = this.get(this.key3);
        if (result == null) {
            this.put122(9, this.newClass(owner), this.newNameType(name, desc));
            result = new Item(this.index++, this.key3);
            this.put(result);
        }
        return result;
    }

    public int newField(String owner, String name, String desc) {
        return this.newFieldItem((String)owner, (String)name, (String)desc).index;
    }

    Item newMethodItem(String owner, String name, String desc, boolean itf) {
        int type = itf ? 11 : 10;
        this.key3.set(type, owner, name, desc);
        Item result = this.get(this.key3);
        if (result == null) {
            this.put122(type, this.newClass(owner), this.newNameType(name, desc));
            result = new Item(this.index++, this.key3);
            this.put(result);
        }
        return result;
    }

    public int newMethod(String owner, String name, String desc, boolean itf) {
        return this.newMethodItem((String)owner, (String)name, (String)desc, (boolean)itf).index;
    }

    Item newInteger(int value) {
        this.key.set(value);
        Item result = this.get(this.key);
        if (result == null) {
            this.pool.putByte(3).putInt(value);
            result = new Item(this.index++, this.key);
            this.put(result);
        }
        return result;
    }

    Item newFloat(float value) {
        this.key.set(value);
        Item result = this.get(this.key);
        if (result == null) {
            this.pool.putByte(4).putInt(this.key.intVal);
            result = new Item(this.index++, this.key);
            this.put(result);
        }
        return result;
    }

    Item newLong(long value) {
        this.key.set(value);
        Item result = this.get(this.key);
        if (result == null) {
            this.pool.putByte(5).putLong(value);
            result = new Item(this.index, this.key);
            this.index += 2;
            this.put(result);
        }
        return result;
    }

    Item newDouble(double value) {
        this.key.set(value);
        Item result = this.get(this.key);
        if (result == null) {
            this.pool.putByte(6).putLong(this.key.longVal);
            result = new Item(this.index, this.key);
            this.index += 2;
            this.put(result);
        }
        return result;
    }

    private Item newString(String value) {
        this.key2.set(8, value, null, null);
        Item result = this.get(this.key2);
        if (result == null) {
            this.pool.put12(8, this.newUTF8(value));
            result = new Item(this.index++, this.key2);
            this.put(result);
        }
        return result;
    }

    public int newNameType(String name, String desc) {
        return this.newNameTypeItem((String)name, (String)desc).index;
    }

    Item newNameTypeItem(String name, String desc) {
        this.key2.set(12, name, desc, null);
        Item result = this.get(this.key2);
        if (result == null) {
            this.put122(12, this.newUTF8(name), this.newUTF8(desc));
            result = new Item(this.index++, this.key2);
            this.put(result);
        }
        return result;
    }

    int addType(String type) {
        this.key.set(30, type, null, null);
        Item result = this.get(this.key);
        if (result == null) {
            result = this.addType(this.key);
        }
        return result.index;
    }

    int addUninitializedType(String type, int offset) {
        this.key.type = 31;
        this.key.intVal = offset;
        this.key.strVal1 = type;
        this.key.hashCode = Integer.MAX_VALUE & 31 + type.hashCode() + offset;
        Item result = this.get(this.key);
        if (result == null) {
            result = this.addType(this.key);
        }
        return result.index;
    }

    private Item addType(Item item) {
        this.typeCount = (short)(this.typeCount + 1);
        Item result = new Item(this.typeCount, this.key);
        this.put(result);
        if (this.typeTable == null) {
            this.typeTable = new Item[16];
        }
        if (this.typeCount == this.typeTable.length) {
            Item[] newTable = new Item[2 * this.typeTable.length];
            System.arraycopy(this.typeTable, 0, newTable, 0, this.typeTable.length);
            this.typeTable = newTable;
        }
        this.typeTable[this.typeCount] = result;
        return result;
    }

    int getMergedType(int type1, int type2) {
        this.key2.type = 32;
        this.key2.longVal = (long)type1 | (long)type2 << 32;
        this.key2.hashCode = Integer.MAX_VALUE & 32 + type1 + type2;
        Item result = this.get(this.key2);
        if (result == null) {
            String t = this.typeTable[type1].strVal1;
            String u = this.typeTable[type2].strVal1;
            this.key2.intVal = this.addType(this.getCommonSuperClass(t, u));
            result = new Item(0, this.key2);
            this.put(result);
        }
        return result.intVal;
    }

    protected String getCommonSuperClass(String type1, String type2) {
        Class<?> d;
        Class<?> c;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        }
        while (!(c = c.getSuperclass()).isAssignableFrom(d)) {
        }
        return c.getName().replace('.', '/');
    }

    private Item get(Item key) {
        Item i = this.items[key.hashCode % this.items.length];
        while (!(i == null || i.type == key.type && key.isEqualTo(i))) {
            i = i.next;
        }
        return i;
    }

    private void put(Item i) {
        if (this.index + this.typeCount > this.threshold) {
            int ll = this.items.length;
            int nl = ll * 2 + 1;
            Item[] newItems = new Item[nl];
            for (int l = ll - 1; l >= 0; --l) {
                Item j = this.items[l];
                while (j != null) {
                    int index = j.hashCode % newItems.length;
                    Item k = j.next;
                    j.next = newItems[index];
                    newItems[index] = j;
                    j = k;
                }
            }
            this.items = newItems;
            this.threshold = (int)((double)nl * 0.75);
        }
        int index = i.hashCode % this.items.length;
        i.next = this.items[index];
        this.items[index] = i;
    }

    private void put122(int b, int s1, int s2) {
        this.pool.put12(b, s1).putShort(s2);
    }

    private void put112(int b1, int b2, int s) {
        this.pool.put11(b1, b2).putShort(s);
    }

    static {
        byte[] b = new byte[220];
        String s = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKSSSSSSSSSSSSSSSSSS";
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte)(s.charAt(i) - 65);
        }
        TYPE = b;
    }
}

