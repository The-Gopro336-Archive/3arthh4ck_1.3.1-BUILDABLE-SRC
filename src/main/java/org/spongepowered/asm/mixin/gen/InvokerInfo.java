package org.spongepowered.asm.mixin.gen;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

public class InvokerInfo
extends AccessorInfo {
    public InvokerInfo(MixinTargetContext mixin, MethodNode method) {
        super(mixin, method, Invoker.class);
    }

    @Override
    protected AccessorInfo.AccessorType initType() {
        return AccessorInfo.AccessorType.METHOD_PROXY;
    }

    @Override
    protected Type initTargetFieldType() {
        return null;
    }

    @Override
    protected MemberInfo initTarget() {
        return new MemberInfo(this.getTargetName(), null, this.method.desc);
    }

    @Override
    public void locate() {
        this.targetMethod = this.findTargetMethod();
    }

    private MethodNode findTargetMethod() {
        return this.findTarget(this.classNode.methods);
    }
}
