package org.spongepowered.asm.mixin.extensibility;

import org.spongepowered.asm.mixin.MixinEnvironment;

public interface IEnvironmentTokenProvider {
    public static final int DEFAULT_PRIORITY = 1000;

    public int getPriority();

    public Integer getToken(String var1, MixinEnvironment var2);
}
