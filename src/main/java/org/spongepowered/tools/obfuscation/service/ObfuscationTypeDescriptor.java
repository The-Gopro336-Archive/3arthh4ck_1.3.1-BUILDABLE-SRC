package org.spongepowered.tools.obfuscation.service;

import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;

public class ObfuscationTypeDescriptor {
    private final String key;
    private final String inputFileArgName;
    private final String extraInputFilesArgName;
    private final String outFileArgName;
    private final Class<? extends ObfuscationEnvironment> environmentType;

    public ObfuscationTypeDescriptor(String key, String inputFileArgName, String outFileArgName, Class<? extends ObfuscationEnvironment> environmentType) {
        this(key, inputFileArgName, null, outFileArgName, environmentType);
    }

    public ObfuscationTypeDescriptor(String key, String inputFileArgName, String extraInputFilesArgName, String outFileArgName, Class<? extends ObfuscationEnvironment> environmentType) {
        this.key = key;
        this.inputFileArgName = inputFileArgName;
        this.extraInputFilesArgName = extraInputFilesArgName;
        this.outFileArgName = outFileArgName;
        this.environmentType = environmentType;
    }

    public final String getKey() {
        return this.key;
    }

    public String getInputFileOption() {
        return this.inputFileArgName;
    }

    public String getExtraInputFilesOption() {
        return this.extraInputFilesArgName;
    }

    public String getOutputFileOption() {
        return this.outFileArgName;
    }

    public Class<? extends ObfuscationEnvironment> getEnvironmentType() {
        return this.environmentType;
    }
}
