package org.spongepowered.tools.obfuscation.validation;

import java.util.Collection;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.spongepowered.tools.obfuscation.MixinValidator;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IMixinValidator;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public class ParentValidator
extends MixinValidator {
    public ParentValidator(IMixinAnnotationProcessor ap) {
        super(ap, IMixinValidator.ValidationPass.EARLY);
    }

    @Override
    public boolean validate(TypeElement mixin, AnnotationHandle annotation, Collection<TypeHandle> targets) {
        if (mixin.getEnclosingElement().getKind() != ElementKind.PACKAGE && !mixin.getModifiers().contains((Object)Modifier.STATIC)) {
            this.error("Inner class mixin must be declared static", mixin);
        }
        return true;
    }
}
