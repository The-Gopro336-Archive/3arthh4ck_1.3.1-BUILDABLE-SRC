package org.spongepowered.asm.lib.commons;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.commons.Remapper;

public class AnnotationRemapper
extends AnnotationVisitor {
    protected final Remapper remapper;

    public AnnotationRemapper(AnnotationVisitor av, Remapper remapper) {
        this(327680, av, remapper);
    }

    protected AnnotationRemapper(int api, AnnotationVisitor av, Remapper remapper) {
        super(api, av);
        this.remapper = remapper;
    }

    public void visit(String name, Object value) {
        this.av.visit(name, this.remapper.mapValue(value));
    }

    public void visitEnum(String name, String desc, String value) {
        this.av.visitEnum(name, this.remapper.mapDesc(desc), value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor v = this.av.visitAnnotation(name, this.remapper.mapDesc(desc));
        return v == null ? null : (v == this.av ? this : new AnnotationRemapper(v, this.remapper));
    }

    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor v = this.av.visitArray(name);
        return v == null ? null : (v == this.av ? this : new AnnotationRemapper(v, this.remapper));
    }
}
