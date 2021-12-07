package org.spongepowered.asm.mixin.struct;

import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

public abstract class SpecialMethodInfo
implements IInjectionPointContext {
    protected final AnnotationNode annotation;
    protected final ClassNode classNode;
    protected final MethodNode method;
    protected final MixinTargetContext mixin;

    public SpecialMethodInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        this.mixin = mixin;
        this.method = method;
        this.annotation = annotation;
        this.classNode = mixin.getTargetClassNode();
    }

    @Override
    public final IMixinContext getContext() {
        return this.mixin;
    }

    @Override
    public final AnnotationNode getAnnotation() {
        return this.annotation;
    }

    public final ClassNode getClassNode() {
        return this.classNode;
    }

    @Override
    public final MethodNode getMethod() {
        return this.method;
    }
}
