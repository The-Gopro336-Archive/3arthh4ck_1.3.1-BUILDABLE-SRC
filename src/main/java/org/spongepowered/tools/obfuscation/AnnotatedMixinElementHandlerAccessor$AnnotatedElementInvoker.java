package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.ExecutableElement;
import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandlerAccessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

class AnnotatedMixinElementHandlerAccessor$AnnotatedElementInvoker
extends AnnotatedMixinElementHandlerAccessor.AnnotatedElementAccessor {
    public AnnotatedMixinElementHandlerAccessor$AnnotatedElementInvoker(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
        super(element, annotation, shouldRemap);
    }

    @Override
    public String getAccessorDesc() {
        return TypeUtils.getDescriptor((ExecutableElement)this.getElement());
    }

    @Override
    public AccessorInfo.AccessorType getAccessorType() {
        return AccessorInfo.AccessorType.METHOD_PROXY;
    }

    @Override
    public String getTargetTypeName() {
        return TypeUtils.getJavaSignature(this.getElement());
    }
}
