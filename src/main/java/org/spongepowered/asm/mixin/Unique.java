package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Unique {
    public boolean silent() default false;
}
