package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.util.Printer;
import org.spongepowered.asm.lib.util.TraceAnnotationVisitor;

public final class TraceFieldVisitor
extends FieldVisitor {
    public final Printer p;

    public TraceFieldVisitor(Printer p) {
        this(null, p);
    }

    public TraceFieldVisitor(FieldVisitor fv, Printer p) {
        super(327680, fv);
        this.p = p;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Printer p = this.p.visitFieldAnnotation(desc, visible);
        AnnotationVisitor av = this.fv == null ? null : this.fv.visitAnnotation(desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitFieldTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = this.fv == null ? null : this.fv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitAttribute(Attribute attr) {
        this.p.visitFieldAttribute(attr);
        super.visitAttribute(attr);
    }

    public void visitEnd() {
        this.p.visitFieldEnd();
        super.visitEnd();
    }
}
