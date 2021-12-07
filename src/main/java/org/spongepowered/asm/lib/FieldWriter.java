package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.AnnotationWriter;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.TypePath;

final class FieldWriter
extends FieldVisitor {
    private final ClassWriter cw;
    private final int access;
    private final int name;
    private final int desc;
    private int signature;
    private int value;
    private AnnotationWriter anns;
    private AnnotationWriter ianns;
    private AnnotationWriter tanns;
    private AnnotationWriter itanns;
    private Attribute attrs;

    FieldWriter(ClassWriter cw, int access, String name, String desc, String signature, Object value) {
        super(327680);
        if (cw.firstField == null) {
            cw.firstField = this;
        } else {
            cw.lastField.fv = this;
        }
        cw.lastField = this;
        this.cw = cw;
        this.access = access;
        this.name = cw.newUTF8(name);
        this.desc = cw.newUTF8(desc);
        if (signature != null) {
            this.signature = cw.newUTF8(signature);
        }
        if (value != null) {
            this.value = cw.newConstItem((Object)value).index;
        }
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
        if (visible) {
            aw.next = this.anns;
            this.anns = aw;
        } else {
            aw.next = this.ianns;
            this.ianns = aw;
        }
        return aw;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.tanns;
            this.tanns = aw;
        } else {
            aw.next = this.itanns;
            this.itanns = aw;
        }
        return aw;
    }

    public void visitAttribute(Attribute attr) {
        attr.next = this.attrs;
        this.attrs = attr;
    }

    public void visitEnd() {
    }

    int getSize() {
        int size = 8;
        if (this.value != 0) {
            this.cw.newUTF8("ConstantValue");
            size += 8;
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            this.cw.newUTF8("Synthetic");
            size += 6;
        }
        if ((this.access & 0x20000) != 0) {
            this.cw.newUTF8("Deprecated");
            size += 6;
        }
        if (this.signature != 0) {
            this.cw.newUTF8("Signature");
            size += 8;
        }
        if (this.anns != null) {
            this.cw.newUTF8("RuntimeVisibleAnnotations");
            size += 8 + this.anns.getSize();
        }
        if (this.ianns != null) {
            this.cw.newUTF8("RuntimeInvisibleAnnotations");
            size += 8 + this.ianns.getSize();
        }
        if (this.tanns != null) {
            this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
            size += 8 + this.tanns.getSize();
        }
        if (this.itanns != null) {
            this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
            size += 8 + this.itanns.getSize();
        }
        if (this.attrs != null) {
            size += this.attrs.getSize(this.cw, null, 0, -1, -1);
        }
        return size;
    }

    void put(ByteVector out) {
        int FACTOR = 64;
        int mask = 0x60000 | (this.access & 0x40000) / 64;
        out.putShort(this.access & ~mask).putShort(this.name).putShort(this.desc);
        int attributeCount = 0;
        if (this.value != 0) {
            ++attributeCount;
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            ++attributeCount;
        }
        if ((this.access & 0x20000) != 0) {
            ++attributeCount;
        }
        if (this.signature != 0) {
            ++attributeCount;
        }
        if (this.anns != null) {
            ++attributeCount;
        }
        if (this.ianns != null) {
            ++attributeCount;
        }
        if (this.tanns != null) {
            ++attributeCount;
        }
        if (this.itanns != null) {
            ++attributeCount;
        }
        if (this.attrs != null) {
            attributeCount += this.attrs.getCount();
        }
        out.putShort(attributeCount);
        if (this.value != 0) {
            out.putShort(this.cw.newUTF8("ConstantValue"));
            out.putInt(2).putShort(this.value);
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            out.putShort(this.cw.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.access & 0x20000) != 0) {
            out.putShort(this.cw.newUTF8("Deprecated")).putInt(0);
        }
        if (this.signature != 0) {
            out.putShort(this.cw.newUTF8("Signature"));
            out.putInt(2).putShort(this.signature);
        }
        if (this.anns != null) {
            out.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
            this.anns.put(out);
        }
        if (this.ianns != null) {
            out.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
            this.ianns.put(out);
        }
        if (this.tanns != null) {
            out.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.tanns.put(out);
        }
        if (this.itanns != null) {
            out.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.itanns.put(out);
        }
        if (this.attrs != null) {
            this.attrs.put(this.cw, null, 0, -1, -1, out);
        }
    }
}
