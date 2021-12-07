package org.spongepowered.asm.mixin.extensibility;

import java.util.Set;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public interface IMixinConfig {
    public static final int DEFAULT_PRIORITY = 1000;

    public MixinEnvironment getEnvironment();

    public String getName();

    public String getMixinPackage();

    public int getPriority();

    public IMixinConfigPlugin getPlugin();

    public boolean isRequired();

    public Set<String> getTargets();
}
