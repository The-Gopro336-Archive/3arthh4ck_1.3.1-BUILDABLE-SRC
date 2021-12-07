package org.spongepowered.tools.obfuscation;

import java.util.Collection;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.spongepowered.tools.obfuscation.AnnotatedMixins;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IMixinValidator;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public abstract class MixinValidator
implements IMixinValidator {
    protected final ProcessingEnvironment processingEnv;
    protected final Messager messager;
    protected final IOptionProvider options;
    protected final IMixinValidator.ValidationPass pass;

    public MixinValidator(IMixinAnnotationProcessor ap, IMixinValidator.ValidationPass pass) {
        this.processingEnv = ap.getProcessingEnvironment();
        this.messager = ap;
        this.options = ap;
        this.pass = pass;
    }

    @Override
    public final boolean validate(IMixinValidator.ValidationPass pass, TypeElement mixin, AnnotationHandle annotation, Collection<TypeHandle> targets) {
        if (pass != this.pass) {
            return true;
        }
        return this.validate(mixin, annotation, targets);
    }

    protected abstract boolean validate(TypeElement var1, AnnotationHandle var2, Collection<TypeHandle> var3);

    protected final void note(String note, Element element) {
        this.messager.printMessage(Diagnostic.Kind.NOTE, note, element);
    }

    protected final void warning(String warning, Element element) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, warning, element);
    }

    protected final void error(String error, Element element) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, error, element);
    }

    protected final Collection<TypeMirror> getMixinsTargeting(TypeMirror targetType) {
        return AnnotatedMixins.getMixinsForEnvironment(this.processingEnv).getMixinsTargeting(targetType);
    }
}
