/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package org.spongepowered.tools.obfuscation;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public final class ObfuscationType {
    private static final Map<String, ObfuscationType> types = new LinkedHashMap<String, ObfuscationType>();
    private final String key;
    private final ObfuscationTypeDescriptor descriptor;
    private final IMixinAnnotationProcessor ap;
    private final IOptionProvider options;

    private ObfuscationType(ObfuscationTypeDescriptor descriptor, IMixinAnnotationProcessor ap) {
        this.key = descriptor.getKey();
        this.descriptor = descriptor;
        this.ap = ap;
        this.options = ap;
    }

    public final ObfuscationEnvironment createEnvironment() {
        try {
            Class<? extends ObfuscationEnvironment> cls = this.descriptor.getEnvironmentType();
            Constructor<? extends ObfuscationEnvironment> ctor = cls.getDeclaredConstructor(ObfuscationType.class);
            ctor.setAccessible(true);
            return ctor.newInstance(this);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        return this.key;
    }

    public String getKey() {
        return this.key;
    }

    public ObfuscationTypeDescriptor getConfig() {
        return this.descriptor;
    }

    public IMixinAnnotationProcessor getAnnotationProcessor() {
        return this.ap;
    }

    public boolean isDefault() {
        String defaultEnv = this.options.getOption("defaultObfuscationEnv");
        return defaultEnv == null && this.key.equals("searge") || defaultEnv != null && this.key.equals(defaultEnv.toLowerCase());
    }

    public boolean isSupported() {
        return this.getInputFileNames().size() > 0;
    }

    public List<String> getInputFileNames() {
        String extraInputFiles;
        ImmutableList.Builder builder = ImmutableList.builder();
        String inputFile = this.options.getOption(this.descriptor.getInputFileOption());
        if (inputFile != null) {
            builder.add((Object)inputFile);
        }
        if ((extraInputFiles = this.options.getOption(this.descriptor.getExtraInputFilesOption())) != null) {
            for (String extraInputFile : extraInputFiles.split(";")) {
                builder.add((Object)extraInputFile.trim());
            }
        }
        return builder.build();
    }

    public String getOutputFileName() {
        return this.options.getOption(this.descriptor.getOutputFileOption());
    }

    public static Iterable<ObfuscationType> types() {
        return types.values();
    }

    public static ObfuscationType create(ObfuscationTypeDescriptor descriptor, IMixinAnnotationProcessor ap) {
        String key = descriptor.getKey();
        if (types.containsKey(key)) {
            throw new IllegalArgumentException("Obfuscation type with key " + key + " was already registered");
        }
        ObfuscationType type = new ObfuscationType(descriptor, ap);
        types.put(key, type);
        return type;
    }

    public static ObfuscationType get(String key) {
        ObfuscationType type = types.get(key);
        if (type == null) {
            throw new IllegalArgumentException("Obfuscation type with key " + key + " was not registered");
        }
        return type;
    }
}

