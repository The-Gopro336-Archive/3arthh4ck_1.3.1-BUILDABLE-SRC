package org.spongepowered.asm.mixin.transformer;

import java.util.List;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.MixinInfo;

class MixinInfo$MixinClassNode
extends ClassNode {
    public final List<MixinInfo.MixinMethodNode> mixinMethods;

    public MixinInfo$MixinClassNode(MixinInfo mixin) {
        this(327680);
    }

    public MixinInfo$MixinClassNode(int api) {
        super(api);
        this.mixinMethods = this.methods;
    }

    public MixinInfo getMixin() {
        return MixinInfo.this;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MixinInfo.MixinMethodNode method = new MixinInfo.MixinMethodNode(MixinInfo.this, access, name, desc, signature, exceptions);
        this.methods.add(method);
        return method;
    }
}
