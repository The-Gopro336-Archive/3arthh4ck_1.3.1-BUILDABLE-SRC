package org.spongepowered.asm.lib.commons;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.commons.AnnotationRemapper;
import org.spongepowered.asm.lib.commons.Remapper;

public class FieldRemapper
extends FieldVisitor {
    private final Remapper remapper;

    public FieldRemapper(FieldVisitor fv, Remapper remapper) {
        this(327680, fv, remapper);
    }

    protected FieldRemapper(int api, FieldVisitor fv, Remapper remapper) {
        super(api, fv);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = this.fv.visitAnnotation(this.remapper.mapDesc(desc), visible);
        return av == null ? null : new AnnotationRemapper(av, this.remapper);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, this.remapper.mapDesc(desc), visible);
        return av == null ? null : new AnnotationRemapper(av, this.remapper);
    }
}
