package org.spongepowered.asm.mixin.transformer;

import java.util.List;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

class MixinInfo$SubType$Standard
extends MixinInfo.SubType {
    MixinInfo$SubType$Standard(MixinInfo info) {
        super(info, "@Mixin", false);
    }

    @Override
    void validate(MixinInfo.State state, List<ClassInfo> targetClasses) {
        MixinInfo.MixinClassNode classNode = state.getClassNode();
        for (ClassInfo targetClass : targetClasses) {
            if (classNode.superName.equals(targetClass.getSuperName())) continue;
            if (!targetClass.hasSuperClass(classNode.superName, ClassInfo.Traversal.SUPER)) {
                ClassInfo superClass = ClassInfo.forName(classNode.superName);
                if (superClass.isMixin()) {
                    for (ClassInfo superTarget : superClass.getTargets()) {
                        if (!targetClasses.contains(superTarget)) continue;
                        throw new InvalidMixinException((IMixinInfo)this.mixin, "Illegal hierarchy detected. Derived mixin " + this + " targets the same class " + superTarget.getClassName() + " as its superclass " + superClass.getClassName());
                    }
                }
                throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class '" + classNode.superName.replace('/', '.') + "' of " + this.mixin.getName() + " was not found in the hierarchy of target class '" + targetClass + "'");
            }
            this.detached = true;
        }
    }

    @Override
    MixinPreProcessorStandard createPreProcessor(MixinInfo.MixinClassNode classNode) {
        return new MixinPreProcessorStandard(this.mixin, classNode);
    }
}
