package org.spongepowered.tools.obfuscation;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.ReferenceManager;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.interfaces.IReferenceManager;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.service.ObfuscationServices;

public class ObfuscationManager
implements IObfuscationManager {
    private final IMixinAnnotationProcessor ap;
    private final List<ObfuscationEnvironment> environments = new ArrayList<ObfuscationEnvironment>();
    private final IObfuscationDataProvider obfs;
    private final IReferenceManager refs;
    private final List<IMappingConsumer> consumers = new ArrayList<IMappingConsumer>();
    private boolean initDone;

    public ObfuscationManager(IMixinAnnotationProcessor ap) {
        this.ap = ap;
        this.obfs = new ObfuscationDataProvider(ap, this.environments);
        this.refs = new ReferenceManager(ap, this.environments);
    }

    @Override
    public void init() {
        if (this.initDone) {
            return;
        }
        this.initDone = true;
        ObfuscationServices.getInstance().initProviders(this.ap);
        for (ObfuscationType obfType : ObfuscationType.types()) {
            if (!obfType.isSupported()) continue;
            this.environments.add(obfType.createEnvironment());
        }
    }

    @Override
    public IObfuscationDataProvider getDataProvider() {
        return this.obfs;
    }

    @Override
    public IReferenceManager getReferenceManager() {
        return this.refs;
    }

    @Override
    public IMappingConsumer createMappingConsumer() {
        Mappings mappings = new Mappings();
        this.consumers.add(mappings);
        return mappings;
    }

    @Override
    public List<ObfuscationEnvironment> getEnvironments() {
        return this.environments;
    }

    @Override
    public void writeMappings() {
        for (ObfuscationEnvironment env : this.environments) {
            env.writeMappings(this.consumers);
        }
    }

    @Override
    public void writeReferences() {
        this.refs.write();
    }
}
