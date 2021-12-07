package org.spongepowered.tools.obfuscation;

import java.lang.reflect.Method;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

class AnnotatedMixinElementHandlerOverwrite
extends AnnotatedMixinElementHandler {
    AnnotatedMixinElementHandlerOverwrite(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
        super(ap, mixin);
    }

    public void registerMerge(ExecutableElement method) {
        this.validateTargetMethod(method, null, new AnnotatedMixinElementHandler.AliasedElementName(method, AnnotationHandle.MISSING), "overwrite", true, true);
    }

    public void registerOverwrite(AnnotatedElementOverwrite elem) {
        AnnotatedMixinElementHandler.AliasedElementName name = new AnnotatedMixinElementHandler.AliasedElementName((Element)elem.getElement(), elem.getAnnotation());
        this.validateTargetMethod((ExecutableElement)elem.getElement(), elem.getAnnotation(), name, "@Overwrite", true, false);
        this.checkConstraints((ExecutableElement)elem.getElement(), elem.getAnnotation());
        if (elem.shouldRemap()) {
            for (TypeHandle target : this.mixin.getTargets()) {
                if (this.registerOverwriteForTarget(elem, target)) continue;
                return;
            }
        }
        if (!"true".equalsIgnoreCase(this.ap.getOption("disableOverwriteChecker"))) {
            Diagnostic.Kind overwriteErrorKind = "error".equalsIgnoreCase(this.ap.getOption("overwriteErrorLevel")) ? Diagnostic.Kind.ERROR : Diagnostic.Kind.WARNING;
            String javadoc = this.ap.getJavadocProvider().getJavadoc((Element)elem.getElement());
            if (javadoc == null) {
                this.ap.printMessage(overwriteErrorKind, "@Overwrite is missing javadoc comment", (Element)elem.getElement());
                return;
            }
            if (!javadoc.toLowerCase().contains("@author")) {
                this.ap.printMessage(overwriteErrorKind, "@Overwrite is missing an @author tag", (Element)elem.getElement());
            }
            if (!javadoc.toLowerCase().contains("@reason")) {
                this.ap.printMessage(overwriteErrorKind, "@Overwrite is missing an @reason tag", (Element)elem.getElement());
            }
        }
    }

    private boolean registerOverwriteForTarget(AnnotatedElementOverwrite elem, TypeHandle target) {
        MappingMethod targetMethod = target.getMappingMethod(elem.getSimpleName(), elem.getDesc());
        ObfuscationData<MappingMethod> obfData = this.obf.getDataProvider().getObfMethod(targetMethod);
        if (obfData.isEmpty()) {
            Diagnostic.Kind error = Diagnostic.Kind.ERROR;
            try {
                Method md = ((ExecutableElement)elem.getElement()).getClass().getMethod("isStatic", new Class[0]);
                if (((Boolean)md.invoke(elem.getElement(), new Object[0])).booleanValue()) {
                    error = Diagnostic.Kind.WARNING;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.ap.printMessage(error, "No obfuscation mapping for @Overwrite method", (Element)elem.getElement());
            return false;
        }
        try {
            this.addMethodMappings(elem.getSimpleName(), elem.getDesc(), obfData);
        }
        catch (Mappings.MappingConflictException ex) {
            elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Mapping conflict for @Overwrite method: " + ex.getNew().getSimpleName() + " for target " + target + " conflicts with existing mapping " + ex.getOld().getSimpleName());
            return false;
        }
        return true;
    }

    static class AnnotatedElementOverwrite
    extends AnnotatedMixinElementHandler.AnnotatedElement<ExecutableElement> {
        private final boolean shouldRemap;

        public AnnotatedElementOverwrite(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
            super(element, annotation);
            this.shouldRemap = shouldRemap;
        }

        public boolean shouldRemap() {
            return this.shouldRemap;
        }
    }
}
