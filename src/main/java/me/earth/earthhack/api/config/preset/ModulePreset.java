package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.module.Module;

public abstract class ModulePreset
implements Config {
    private final Module module;
    private final String name;
    private final String description;

    public ModulePreset(String name, Module module, String description) {
        this.name = name;
        this.module = module;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Module getModule() {
        return this.module;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (o instanceof ModulePreset) {
            ModulePreset other = (ModulePreset)o;
            return other.name.equals(this.name) && other.module.equals(this.module);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
