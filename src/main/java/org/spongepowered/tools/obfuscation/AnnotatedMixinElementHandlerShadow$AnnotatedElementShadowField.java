package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.VariableElement;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandlerShadow;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

class AnnotatedMixinElementHandlerShadow$AnnotatedElementShadowField
extends AnnotatedMixinElementHandlerShadow.AnnotatedElementShadow<VariableElement, MappingField> {
    public AnnotatedMixinElementHandlerShadow$AnnotatedElementShadowField(VariableElement element, AnnotationHandle annotation, boolean shouldRemap) {
        super(element, annotation, shouldRemap, IMapping.Type.FIELD);
    }

    @Override
    public MappingField getMapping(TypeHandle owner, String name, String desc) {
        return new MappingField(owner.getName(), name, desc);
    }

    @Override
    public void addMapping(ObfuscationType type, IMapping<?> remapped) {
        AnnotatedMixinElementHandlerShadow.this.addFieldMapping(type, this.setObfuscatedName(remapped), this.getDesc(), remapped.getDesc());
    }
}
