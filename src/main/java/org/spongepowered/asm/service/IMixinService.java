package org.spongepowered.asm.service;

import java.io.InputStream;
import java.util.Collection;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.util.ReEntranceLock;

public interface IMixinService {
    public String getName();

    public boolean isValid();

    public void prepare();

    public MixinEnvironment.Phase getInitialPhase();

    public void init();

    public void beginPhase();

    public void checkEnv(Object var1);

    public ReEntranceLock getReEntranceLock();

    public IClassProvider getClassProvider();

    public IClassBytecodeProvider getBytecodeProvider();

    public Collection<String> getPlatformAgents();

    public InputStream getResourceAsStream(String var1);

    public void registerInvalidClass(String var1);

    public boolean isClassLoaded(String var1);

    public String getClassRestrictions(String var1);

    public Collection<ITransformer> getTransformers();

    public String getSideName();
}
