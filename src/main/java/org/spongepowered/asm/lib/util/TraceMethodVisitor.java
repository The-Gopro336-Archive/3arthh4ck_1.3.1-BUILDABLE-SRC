package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.util.Printer;
import org.spongepowered.asm.lib.util.TraceAnnotationVisitor;

public final class TraceMethodVisitor
extends MethodVisitor {
    public final Printer p;

    public TraceMethodVisitor(Printer p) {
        this(null, p);
    }

    public TraceMethodVisitor(MethodVisitor mv, Printer p) {
        super(327680, mv);
        this.p = p;
    }

    public void visitParameter(String name, int access) {
        this.p.visitParameter(name, access);
        super.visitParameter(name, access);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Printer p = this.p.visitMethodAnnotation(desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitAnnotation(desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitMethodTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitAttribute(Attribute attr) {
        this.p.visitMethodAttribute(attr);
        super.visitAttribute(attr);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        Printer p = this.p.visitAnnotationDefault();
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitAnnotationDefault();
        return new TraceAnnotationVisitor(av, p);
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        Printer p = this.p.visitParameterAnnotation(parameter, desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitParameterAnnotation(parameter, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitCode() {
        this.p.visitCode();
        super.visitCode();
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        this.p.visitFrame(type, nLocal, local, nStack, stack);
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    public void visitInsn(int opcode) {
        this.p.visitInsn(opcode);
        super.visitInsn(opcode);
    }

    public void visitIntInsn(int opcode, int operand) {
        this.p.visitIntInsn(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn(int opcode, int var) {
        this.p.visitVarInsn(opcode, var);
        super.visitVarInsn(opcode, var);
    }

    public void visitTypeInsn(int opcode, String type) {
        this.p.visitTypeInsn(opcode, type);
        super.visitTypeInsn(opcode, type);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.p.visitFieldInsn(opcode, owner, name, desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.p.visitMethodInsn(opcode, owner, name, desc);
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.p.visitMethodInsn(opcode, owner, name, desc, itf);
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object ... bsmArgs) {
        this.p.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.p.visitJumpInsn(opcode, label);
        super.visitJumpInsn(opcode, label);
    }

    public void visitLabel(Label label) {
        this.p.visitLabel(label);
        super.visitLabel(label);
    }

    public void visitLdcInsn(Object cst) {
        this.p.visitLdcInsn(cst);
        super.visitLdcInsn(cst);
    }

    public void visitIincInsn(int var, int increment) {
        this.p.visitIincInsn(var, increment);
        super.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.p.visitTableSwitchInsn(min, max, dflt, labels);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.p.visitLookupSwitchInsn(dflt, keys, labels);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.p.visitMultiANewArrayInsn(desc, dims);
        super.visitMultiANewArrayInsn(desc, dims);
    }

    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitInsnAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.p.visitTryCatchBlock(start, end, handler, type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        this.p.visitLocalVariable(name, desc, signature, start, end, index);
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        Printer p = this.p.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        AnnotationVisitor av = this.mv == null ? null : this.mv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    public void visitLineNumber(int line, Label start) {
        this.p.visitLineNumber(line, start);
        super.visitLineNumber(line, start);
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        this.p.visitMaxs(maxStack, maxLocals);
        super.visitMaxs(maxStack, maxLocals);
    }

    public void visitEnd() {
        this.p.visitMethodEnd();
        super.visitEnd();
    }
}
