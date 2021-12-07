package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.CLASS)
public @interface Mixin {
    public Class<?>[] value() default {};

    public String[] targets() default {};

    public int priority() default 1000;

    public boolean remap() default true;
}
