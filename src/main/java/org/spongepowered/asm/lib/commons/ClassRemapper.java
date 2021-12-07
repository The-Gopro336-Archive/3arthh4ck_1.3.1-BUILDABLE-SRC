package org.spongepowered.asm.lib.commons;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.commons.AnnotationRemapper;
import org.spongepowered.asm.lib.commons.FieldRemapper;
import org.spongepowered.asm.lib.commons.MethodRemapper;
import org.spongepowered.asm.lib.commons.Remapper;

public class ClassRemapper
extends ClassVisitor {
    protected final Remapper remapper;
    protected String className;

    public ClassRemapper(ClassVisitor cv, Remapper remapper) {
        this(327680, cv, remapper);
    }

    protected ClassRemapper(int api, ClassVisitor cv, Remapper remapper) {
        super(api, cv);
        this.remapper = remapper;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, this.remapper.mapType(name), this.remapper.mapSignature(signature, false), this.remapper.mapType(superName), interfaces == null ? null : this.remapper.mapTypes(interfaces));
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(this.remapper.mapDesc(desc), visible);
        return av == null ? null : this.createAnnotationRemapper(av);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, this.remapper.mapDesc(desc), visible);
        return av == null ? null : this.createAnnotationRemapper(av);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, this.remapper.mapFieldName(this.className, name, desc), this.remapper.mapDesc(desc), this.remapper.mapSignature(signature, true), this.remapper.mapValue(value));
        return fv == null ? null : this.createFieldRemapper(fv);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String newDesc = this.remapper.mapMethodDesc(desc);
        MethodVisitor mv = super.visitMethod(access, this.remapper.mapMethodName(this.className, name, desc), newDesc, this.remapper.mapSignature(signature, false), exceptions == null ? null : this.remapper.mapTypes(exceptions));
        return mv == null ? null : this.createMethodRemapper(mv);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(this.remapper.mapType(name), outerName == null ? null : this.remapper.mapType(outerName), innerName, access);
    }

    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(this.remapper.mapType(owner), name == null ? null : this.remapper.mapMethodName(owner, name, desc), desc == null ? null : this.remapper.mapMethodDesc(desc));
    }

    protected FieldVisitor createFieldRemapper(FieldVisitor fv) {
        return new FieldRemapper(fv, this.remapper);
    }

    protected MethodVisitor createMethodRemapper(MethodVisitor mv) {
        return new MethodRemapper(mv, this.remapper);
    }

    protected AnnotationVisitor createAnnotationRemapper(AnnotationVisitor av) {
        return new AnnotationRemapper(av, this.remapper);
    }
}
