package org.spongepowered.tools.obfuscation;

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.tools.obfuscation.MixinObfuscationProcessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

@SupportedAnnotationTypes(value={"org.spongepowered.asm.mixin.Mixin", "org.spongepowered.asm.mixin.Shadow", "org.spongepowered.asm.mixin.Overwrite", "org.spongepowered.asm.mixin.gen.Accessor", "org.spongepowered.asm.mixin.Implements"})
public class MixinObfuscationProcessorTargets
extends MixinObfuscationProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.postProcess(roundEnv);
            return true;
        }
        this.processMixins(roundEnv);
        this.processShadows(roundEnv);
        this.processOverwrites(roundEnv);
        this.processAccessors(roundEnv);
        this.processInvokers(roundEnv);
        this.processImplements(roundEnv);
        this.postProcess(roundEnv);
        return true;
    }

    @Override
    protected void postProcess(RoundEnvironment roundEnv) {
        super.postProcess(roundEnv);
        try {
            this.mixins.writeReferences();
            this.mixins.writeMappings();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processShadows(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Shadow.class)) {
            Element parent = element.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), element);
                continue;
            }
            AnnotationHandle shadow = AnnotationHandle.of(element, Shadow.class);
            if (element.getKind() == ElementKind.FIELD) {
                this.mixins.registerShadow((TypeElement)parent, (VariableElement)element, shadow);
                continue;
            }
            if (element.getKind() == ElementKind.METHOD) {
                this.mixins.registerShadow((TypeElement)parent, (ExecutableElement)element, shadow);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method or field", element);
        }
    }

    private void processOverwrites(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Overwrite.class)) {
            Element parent = element.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), element);
                continue;
            }
            if (element.getKind() == ElementKind.METHOD) {
                this.mixins.registerOverwrite((TypeElement)parent, (ExecutableElement)element);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", element);
        }
    }

    private void processAccessors(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Accessor.class)) {
            Element parent = element.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), element);
                continue;
            }
            if (element.getKind() == ElementKind.METHOD) {
                this.mixins.registerAccessor((TypeElement)parent, (ExecutableElement)element);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", element);
        }
    }

    private void processInvokers(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Invoker.class)) {
            Element parent = element.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                this.mixins.printMessage(Diagnostic.Kind.ERROR, "Unexpected parent with type " + TypeUtils.getElementType(parent), element);
                continue;
            }
            if (element.getKind() == ElementKind.METHOD) {
                this.mixins.registerInvoker((TypeElement)parent, (ExecutableElement)element);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Element is not a method", element);
        }
    }

    private void processImplements(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Implements.class)) {
            if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE) {
                AnnotationHandle implementsAnnotation = AnnotationHandle.of(element, Implements.class);
                this.mixins.registerSoftImplements((TypeElement)element, implementsAnnotation);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.ERROR, "Found an @Implements annotation on an element which is not a class or interface", element);
        }
    }
}
