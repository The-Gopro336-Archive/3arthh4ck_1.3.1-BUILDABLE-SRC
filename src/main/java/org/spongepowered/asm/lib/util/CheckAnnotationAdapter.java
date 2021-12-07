package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.util.CheckMethodAdapter;

public class CheckAnnotationAdapter
extends AnnotationVisitor {
    private final boolean named;
    private boolean end;

    public CheckAnnotationAdapter(AnnotationVisitor av) {
        this(av, true);
    }

    CheckAnnotationAdapter(AnnotationVisitor av, boolean named) {
        super(327680, av);
        this.named = named;
    }

    public void visit(String name, Object value) {
        int sort;
        this.checkEnd();
        this.checkName(name);
        if (!(value instanceof Byte || value instanceof Boolean || value instanceof Character || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof String || value instanceof Type || value instanceof byte[] || value instanceof boolean[] || value instanceof char[] || value instanceof short[] || value instanceof int[] || value instanceof long[] || value instanceof float[] || value instanceof double[])) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        if (value instanceof Type && (sort = ((Type)value).getSort()) == 11) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        if (this.av != null) {
            this.av.visit(name, value);
        }
    }

    public void visitEnum(String name, String desc, String value) {
        this.checkEnd();
        this.checkName(name);
        CheckMethodAdapter.checkDesc(desc, false);
        if (value == null) {
            throw new IllegalArgumentException("Invalid enum value");
        }
        if (this.av != null) {
            this.av.visitEnum(name, desc, value);
        }
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        this.checkEnd();
        this.checkName(name);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(this.av == null ? null : this.av.visitAnnotation(name, desc));
    }

    public AnnotationVisitor visitArray(String name) {
        this.checkEnd();
        this.checkName(name);
        return new CheckAnnotationAdapter(this.av == null ? null : this.av.visitArray(name), false);
    }

    public void visitEnd() {
        this.checkEnd();
        this.end = true;
        if (this.av != null) {
            this.av.visitEnd();
        }
    }

    private void checkEnd() {
        if (this.end) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }

    private void checkName(String name) {
        if (this.named && name == null) {
            throw new IllegalArgumentException("Annotation value name must not be null");
        }
    }
}
