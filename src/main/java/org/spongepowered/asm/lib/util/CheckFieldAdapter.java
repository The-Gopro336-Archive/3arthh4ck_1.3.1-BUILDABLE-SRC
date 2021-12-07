package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.util.CheckAnnotationAdapter;
import org.spongepowered.asm.lib.util.CheckClassAdapter;
import org.spongepowered.asm.lib.util.CheckMethodAdapter;

public class CheckFieldAdapter
extends FieldVisitor {
    private boolean end;

    public CheckFieldAdapter(FieldVisitor fv) {
        this(327680, fv);
        if (this.getClass() != CheckFieldAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected CheckFieldAdapter(int api, FieldVisitor fv) {
        super(api, fv);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.checkEnd();
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(desc, visible));
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        this.checkEnd();
        int sort = typeRef >>> 24;
        if (sort != 19) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef, typePath, desc, visible));
    }

    public void visitAttribute(Attribute attr) {
        this.checkEnd();
        if (attr == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attr);
    }

    public void visitEnd() {
        this.checkEnd();
        this.end = true;
        super.visitEnd();
    }

    private void checkEnd() {
        if (this.end) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }
}
