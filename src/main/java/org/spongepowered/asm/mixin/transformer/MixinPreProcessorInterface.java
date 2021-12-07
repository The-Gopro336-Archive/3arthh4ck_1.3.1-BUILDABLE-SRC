package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidInterfaceMixinException;
import org.spongepowered.asm.util.Bytecode;

class MixinPreProcessorInterface
extends MixinPreProcessorStandard {
    MixinPreProcessorInterface(MixinInfo mixin, MixinInfo.MixinClassNode classNode) {
        super(mixin, classNode);
    }

    @Override
    protected void prepareMethod(MixinInfo.MixinMethodNode mixinMethod, ClassInfo.Method method) {
        if (!Bytecode.hasFlag(mixinMethod, 1) && !Bytecode.hasFlag(mixinMethod, 4096)) {
            throw new InvalidInterfaceMixinException((IMixinInfo)this.mixin, "Interface mixin contains a non-public method! Found " + method + " in " + this.mixin);
        }
        super.prepareMethod(mixinMethod, method);
    }

    @Override
    protected boolean validateField(MixinTargetContext context, FieldNode field, AnnotationNode shadow) {
        if (!Bytecode.hasFlag(field, 8)) {
            throw new InvalidInterfaceMixinException((IMixinInfo)this.mixin, "Interface mixin contains an instance field! Found " + field.name + " in " + this.mixin);
        }
        return super.validateField(context, field, shadow);
    }
}
