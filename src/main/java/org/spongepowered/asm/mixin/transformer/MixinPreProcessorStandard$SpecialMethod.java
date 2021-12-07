package org.spongepowered.asm.mixin.transformer;

import java.lang.annotation.Annotation;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.util.Bytecode;

enum MixinPreProcessorStandard$SpecialMethod {
    MERGE(true),
    OVERWRITE(true, Overwrite.class),
    SHADOW(false, Shadow.class),
    ACCESSOR(false, Accessor.class),
    INVOKER(false, Invoker.class);

    final boolean isOverwrite;
    final Class<? extends Annotation> annotation;
    final String description;

    private MixinPreProcessorStandard$SpecialMethod(boolean isOverwrite, Class<? extends Annotation> type) {
        this.isOverwrite = isOverwrite;
        this.annotation = type;
        this.description = "@" + Bytecode.getSimpleName(type);
    }

    private MixinPreProcessorStandard$SpecialMethod(boolean isOverwrite) {
        this.isOverwrite = isOverwrite;
        this.annotation = null;
        this.description = "overwrite";
    }

    public String toString() {
        return this.description;
    }
}
