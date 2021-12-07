package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.List;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.InterfaceInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.throwables.MixinReloadException;

class MixinInfo$Reloaded
extends MixinInfo.State {
    private final MixinInfo.State previous;

    MixinInfo$Reloaded(MixinInfo.State previous, byte[] mixinBytes) {
        super(MixinInfo.this, mixinBytes, previous.getClassInfo());
        this.previous = previous;
    }

    @Override
    protected void validateChanges(MixinInfo.SubType type, List<ClassInfo> targetClasses) {
        if (!this.syntheticInnerClasses.equals(this.previous.syntheticInnerClasses)) {
            throw new MixinReloadException(MixinInfo.this, "Cannot change inner classes");
        }
        if (!this.interfaces.equals(this.previous.interfaces)) {
            throw new MixinReloadException(MixinInfo.this, "Cannot change interfaces");
        }
        if (!new HashSet(this.softImplements).equals(new HashSet<InterfaceInfo>(this.previous.softImplements))) {
            throw new MixinReloadException(MixinInfo.this, "Cannot change soft interfaces");
        }
        List<ClassInfo> targets = MixinInfo.this.readTargetClasses(this.classNode, true);
        if (!new HashSet<ClassInfo>(targets).equals(new HashSet<ClassInfo>(targetClasses))) {
            throw new MixinReloadException(MixinInfo.this, "Cannot change target classes");
        }
        int priority = MixinInfo.this.readPriority(this.classNode);
        if (priority != MixinInfo.this.getPriority()) {
            throw new MixinReloadException(MixinInfo.this, "Cannot change mixin priority");
        }
    }
}
