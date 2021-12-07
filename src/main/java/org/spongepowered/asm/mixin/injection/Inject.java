package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Inject {
    public String id() default "";

    public String[] method();

    public Slice[] slice() default {};

    public At[] at();

    public boolean cancellable() default false;

    public LocalCapture locals() default LocalCapture.NO_CAPTURE;

    public boolean remap() default true;

    public int require() default -1;

    public int expect() default 1;

    public int allow() default -1;

    public String constraints() default "";
}
