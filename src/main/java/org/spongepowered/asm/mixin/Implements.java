package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.spongepowered.asm.mixin.Interface;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.CLASS)
public @interface Implements {
    public Interface[] value();
}
