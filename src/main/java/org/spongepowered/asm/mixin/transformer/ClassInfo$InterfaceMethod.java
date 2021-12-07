package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.ClassInfo;

public class ClassInfo$InterfaceMethod
extends ClassInfo.Method {
    private final ClassInfo owner;

    public ClassInfo$InterfaceMethod(ClassInfo.Member member) {
        super(ClassInfo.this, member);
        this.owner = member.getOwner();
    }

    @Override
    public ClassInfo getOwner() {
        return this.owner;
    }

    @Override
    public ClassInfo getImplementor() {
        return ClassInfo.this;
    }
}
