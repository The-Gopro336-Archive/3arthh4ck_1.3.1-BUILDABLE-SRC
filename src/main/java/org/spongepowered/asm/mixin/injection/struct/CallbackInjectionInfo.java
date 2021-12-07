package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Strings;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInjector;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;

public class CallbackInjectionInfo
extends InjectionInfo {
    protected CallbackInjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        super(mixin, method, annotation);
    }

    @Override
    protected Injector parseInjector(AnnotationNode injectAnnotation) {
        boolean cancellable = Annotations.getValue(injectAnnotation, "cancellable", Boolean.FALSE);
        LocalCapture locals = Annotations.getValue(injectAnnotation, "locals", LocalCapture.class, LocalCapture.NO_CAPTURE);
        String identifier = Annotations.getValue(injectAnnotation, "id", "");
        return new CallbackInjector(this, cancellable, locals, identifier);
    }

    @Override
    public String getSliceId(String id) {
        return Strings.nullToEmpty(id);
    }
}
