package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER})
public @interface Coerce {
}
