package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.ExecutableElement;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandlerShadow;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

class AnnotatedMixinElementHandlerShadow$AnnotatedElementShadowMethod
extends AnnotatedMixinElementHandlerShadow.AnnotatedElementShadow<ExecutableElement, MappingMethod> {
    public AnnotatedMixinElementHandlerShadow$AnnotatedElementShadowMethod(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
        super(element, annotation, shouldRemap, IMapping.Type.METHOD);
    }

    @Override
    public MappingMethod getMapping(TypeHandle owner, String name, String desc) {
        return owner.getMappingMethod(name, desc);
    }

    @Override
    public void addMapping(ObfuscationType type, IMapping<?> remapped) {
        AnnotatedMixinElementHandlerShadow.this.addMethodMapping(type, this.setObfuscatedName(remapped), this.getDesc(), remapped.getDesc());
    }
}
