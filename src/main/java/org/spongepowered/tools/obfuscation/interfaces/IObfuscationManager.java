package org.spongepowered.tools.obfuscation.interfaces;

import java.util.List;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.interfaces.IReferenceManager;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

public interface IObfuscationManager {
    public void init();

    public IObfuscationDataProvider getDataProvider();

    public IReferenceManager getReferenceManager();

    public IMappingConsumer createMappingConsumer();

    public List<ObfuscationEnvironment> getEnvironments();

    public void writeMappings();

    public void writeReferences();
}
