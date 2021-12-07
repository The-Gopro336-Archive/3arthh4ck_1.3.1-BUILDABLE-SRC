package org.spongepowered.asm.service;

public interface IMixinServiceBootstrap {
    public String getName();

    public String getServiceClassName();

    public void bootstrap();
}
