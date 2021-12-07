package org.spongepowered.asm.mixin.extensibility;

import java.util.List;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;

public interface IMixinInfo {
    public IMixinConfig getConfig();

    public String getName();

    public String getClassName();

    public String getClassRef();

    public byte[] getClassBytes();

    public boolean isDetachedSuper();

    public ClassNode getClassNode(int var1);

    public List<String> getTargetClasses();

    public int getPriority();

    public MixinEnvironment.Phase getPhase();
}
