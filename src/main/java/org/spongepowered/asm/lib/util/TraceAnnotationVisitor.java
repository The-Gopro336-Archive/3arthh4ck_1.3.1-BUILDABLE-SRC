package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.util.Printer;

public final class TraceAnnotationVisitor
extends AnnotationVisitor {
    private final Printer p;

    public TraceAnnotationVisitor(Printer p) {
        this(null, p);
    }

    public TraceAnnotationVisitor(AnnotationVisitor av, Printer p) {
        super(327680, av);
        this.p = p;
    }

    public void visit(String name, Object value) {
        this.p.visit(name, value);
        super.visit(name, value);
    }

    public void visitEnum(String name, String desc, String value) {
        this.p.visitEnum(name, desc, value);
        super.visitEnum(name, desc, value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        Printer p = this.p.visitAnnotation(name, desc);
        AnnotationVisitor av = this.av == null ? null : this.av.visitAnnotation(name, desc);
        return new TraceAnnotationVisitor(av, p);
    }

    public AnnotationVisitor visitArray(String name) {
        Printer p = this.p.visitArray(name);
        AnnotationVisitor av = this.av == null ? null : this.av.visitArray(name);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitEnd() {
        this.p.visitAnnotationEnd();
        super.visitEnd();
    }
}
