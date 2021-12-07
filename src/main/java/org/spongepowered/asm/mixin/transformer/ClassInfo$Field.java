package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.Annotations;

class ClassInfo$Field
extends ClassInfo.Member {
    public ClassInfo$Field(ClassInfo.Member member) {
        super(member);
    }

    public ClassInfo$Field(FieldNode field) {
        this(field, false);
    }

    public ClassInfo$Field(FieldNode field, boolean injected) {
        super(ClassInfo.Member.Type.FIELD, field.name, field.desc, field.access, injected);
        this.setUnique(Annotations.getVisible(field, Unique.class) != null);
        if (Annotations.getVisible(field, Shadow.class) != null) {
            boolean decoratedFinal = Annotations.getVisible(field, Final.class) != null;
            boolean decoratedMutable = Annotations.getVisible(field, Mutable.class) != null;
            this.setDecoratedFinal(decoratedFinal, decoratedMutable);
        }
    }

    public ClassInfo$Field(String name, String desc, int access) {
        super(ClassInfo.Member.Type.FIELD, name, desc, access, false);
    }

    public ClassInfo$Field(String name, String desc, int access, boolean injected) {
        super(ClassInfo.Member.Type.FIELD, name, desc, access, injected);
    }

    @Override
    public ClassInfo getOwner() {
        return ClassInfo.this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassInfo$Field)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected String getDisplayFormat() {
        return "%s:%s";
    }
}
