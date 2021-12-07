package org.spongepowered.tools.obfuscation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

abstract class AnnotatedMixinElementHandler$AnnotatedElement<E extends Element> {
    protected final E element;
    protected final AnnotationHandle annotation;
    private final String desc;

    public AnnotatedMixinElementHandler$AnnotatedElement(E element, AnnotationHandle annotation) {
        this.element = element;
        this.annotation = annotation;
        this.desc = TypeUtils.getDescriptor(element);
    }

    public E getElement() {
        return this.element;
    }

    public AnnotationHandle getAnnotation() {
        return this.annotation;
    }

    public String getSimpleName() {
        return this.getElement().getSimpleName().toString();
    }

    public String getDesc() {
        return this.desc;
    }

    public final void printMessage(Messager messager, Diagnostic.Kind kind, CharSequence msg) {
        messager.printMessage(kind, msg, (Element)this.element, this.annotation.asMirror());
    }
}
