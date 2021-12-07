package org.spongepowered.tools.obfuscation.interfaces;

import java.util.Collection;
import javax.lang.model.element.TypeElement;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public interface IMixinValidator {
    public boolean validate(ValidationPass var1, TypeElement var2, AnnotationHandle var3, Collection<TypeHandle> var4);

    public static enum ValidationPass {
        EARLY,
        LATE,
        FINAL;

    }
}
