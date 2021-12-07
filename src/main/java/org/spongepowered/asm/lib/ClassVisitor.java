/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.TypePath;

public abstract class ClassVisitor {
    protected final int api;
    protected ClassVisitor cv;

    public ClassVisitor(int api) {
        this(api, null);
    }

    public ClassVisitor(int api, ClassVisitor cv) {
        if (api != 262144 && api != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.cv = cv;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.cv != null) {
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

    public void visitSource(String source, String debug) {
        if (this.cv != null) {
            this.cv.visitSource(source, debug);
        }
    }

    public void visitOuterClass(String owner, String name, String desc) {
        if (this.cv != null) {
            this.cv.visitOuterClass(owner, name, desc);
        }
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (this.cv != null) {
            return this.cv.visitAnnotation(desc, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.cv != null) {
            return this.cv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    public void visitAttribute(Attribute attr) {
        if (this.cv != null) {
            this.cv.visitAttribute(attr);
        }
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (this.cv != null) {
            this.cv.visitInnerClass(name, outerName, innerName, access);
        }
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (this.cv != null) {
            return this.cv.visitField(access, name, desc, signature, value);
        }
        return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (this.cv != null) {
            return this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
        return null;
    }

    public void visitEnd() {
        if (this.cv != null) {
            this.cv.visitEnd();
        }
    }
}

