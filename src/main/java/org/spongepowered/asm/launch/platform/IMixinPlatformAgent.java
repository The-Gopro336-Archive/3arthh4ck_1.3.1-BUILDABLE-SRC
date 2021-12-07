package org.spongepowered.asm.launch.platform;

public interface IMixinPlatformAgent {
    public String getPhaseProvider();

    public void prepare();

    public void initPrimaryContainer();

    public void inject();

    public String getLaunchTarget();
}
