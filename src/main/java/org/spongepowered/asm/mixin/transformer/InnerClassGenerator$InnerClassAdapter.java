package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.commons.ClassRemapper;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.InnerClassGenerator;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

class InnerClassGenerator$InnerClassAdapter
extends ClassRemapper {
    private final InnerClassGenerator.InnerClassInfo info;

    public InnerClassGenerator$InnerClassAdapter(ClassVisitor cv, InnerClassGenerator.InnerClassInfo info) {
        super(327680, cv, info);
        this.info = info;
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
        AnnotationVisitor av = this.cv.visitAnnotation("Lorg/spongepowered/asm/mixin/transformer/meta/MixinInner;", false);
        av.visit("mixin", this.info.getOwner().toString());
        av.visit("name", this.info.getOriginalName().substring(this.info.getOriginalName().lastIndexOf(47) + 1));
        av.visitEnd();
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (name.startsWith(this.info.getOriginalName() + "$")) {
            throw new InvalidMixinException((IMixinInfo)this.info.getOwner(), "Found unsupported nested inner class " + name + " in " + this.info.getOriginalName());
        }
        super.visitInnerClass(name, outerName, innerName, access);
    }
}
