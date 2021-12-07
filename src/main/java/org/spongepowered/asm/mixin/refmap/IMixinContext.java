package org.spongepowered.asm.mixin.refmap;

import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;

public interface IMixinContext {
    public IMixinInfo getMixin();

    public Extensions getExtensions();

    public String getClassName();

    public String getClassRef();

    public String getTargetClassRef();

    public IReferenceMapper getReferenceMapper();

    public boolean getOption(MixinEnvironment.Option var1);

    public int getPriority();

    public Target getTargetMethod(MethodNode var1);
}
