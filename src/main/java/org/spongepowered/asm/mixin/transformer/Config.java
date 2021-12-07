package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinConfig;

public class Config {
    private final String name;
    private final MixinConfig config;

    public Config(MixinConfig config) {
        this.name = config.getName();
        this.config = config;
    }

    public String getName() {
        return this.name;
    }

    MixinConfig get() {
        return this.config;
    }

    public boolean isVisited() {
        return this.config.isVisited();
    }

    public IMixinConfig getConfig() {
        return this.config;
    }

    public MixinEnvironment getEnvironment() {
        return this.config.getEnvironment();
    }

    public String toString() {
        return this.config.toString();
    }

    public boolean equals(Object obj) {
        return obj instanceof Config && this.name.equals(((Config)obj).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Deprecated
    public static Config create(String configFile, MixinEnvironment outer) {
        return MixinConfig.create(configFile, outer);
    }

    public static Config create(String configFile) {
        return MixinConfig.create(configFile, MixinEnvironment.getDefaultEnvironment());
    }
}
