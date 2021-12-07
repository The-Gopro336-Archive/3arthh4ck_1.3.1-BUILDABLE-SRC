package org.spongepowered.tools.obfuscation;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.tools.obfuscation.MixinObfuscationProcessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

@SupportedAnnotationTypes(value={"org.spongepowered.asm.mixin.injection.Inject", "org.spongepowered.asm.mixin.injection.ModifyArg", "org.spongepowered.asm.mixin.injection.ModifyArgs", "org.spongepowered.asm.mixin.injection.Redirect", "org.spongepowered.asm.mixin.injection.At"})
public class MixinObfuscationProcessorInjection
extends MixinObfuscationProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.postProcess(roundEnv);
            return true;
        }
        this.processMixins(roundEnv);
        this.processInjectors(roundEnv, Inject.class);
        this.processInjectors(roundEnv, ModifyArg.class);
        this.processInjectors(roundEnv, ModifyArgs.class);
        this.processInjectors(roundEnv, Redirect.class);
        this.processInjectors(roundEnv, ModifyVariable.class);
        this.processInjectors(roundEnv, ModifyConstant.class);
        this.postProcess(roundEnv);
        return true;
    }

    @Override
    protected void postProcess(RoundEnvironment roundEnv) {
        super.postProcess(roundEnv);
        try {
            this.mixins.writeReferences();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processInjectors(RoundEnvironment roundEnv, Class<? extends Annotation> injectorClass) {
        for (Element element : roundEnv.getElementsAnnotatedWith(injectorClass)) {
            Element parent = element.getEnclosingElement();
            if (!(parent instanceof TypeElement)) {
                throw new IllegalStateException("@" + injectorClass.getSimpleName() + " element has unexpected parent with type " + TypeUtils.getElementType(parent));
            }
            AnnotationHandle inject = AnnotationHandle.of(element, injectorClass);
            if (element.getKind() == ElementKind.METHOD) {
                this.mixins.registerInjector((TypeElement)parent, (ExecutableElement)element, inject);
                continue;
            }
            this.mixins.printMessage(Diagnostic.Kind.WARNING, "Found an @" + injectorClass.getSimpleName() + " annotation on an element which is not a method: " + element.toString());
        }
    }
}
