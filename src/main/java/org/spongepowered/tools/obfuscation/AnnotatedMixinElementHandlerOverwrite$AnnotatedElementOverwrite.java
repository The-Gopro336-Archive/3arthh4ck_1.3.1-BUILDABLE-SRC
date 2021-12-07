package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.ExecutableElement;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;

class AnnotatedMixinElementHandlerOverwrite$AnnotatedElementOverwrite
extends AnnotatedMixinElementHandler.AnnotatedElement<ExecutableElement> {
    private final boolean shouldRemap;

    public AnnotatedMixinElementHandlerOverwrite$AnnotatedElementOverwrite(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
        super(element, annotation);
        this.shouldRemap = shouldRemap;
    }

    public boolean shouldRemap() {
        return this.shouldRemap;
    }
}
