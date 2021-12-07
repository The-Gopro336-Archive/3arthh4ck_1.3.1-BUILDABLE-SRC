package me.earth.earthhack.api.config.preset;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.config.preset.ValuePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.util.IdentifiedNameable;

public class ModuleConfig
extends IdentifiedNameable
implements Config {
    private List<ValuePreset> presets = new ArrayList<ValuePreset>();

    public ModuleConfig(String name) {
        super(name);
    }

    public void setPresets(List<ValuePreset> presets) {
        if (presets != null) {
            this.presets = presets;
        }
    }

    public List<ValuePreset> getPresets() {
        return this.presets;
    }

    @Override
    public void apply() {
        for (ModulePreset modulePreset : this.presets) {
            modulePreset.apply();
        }
    }

    public static ModuleConfig create(String name, Register<Module> modules) {
        ModuleConfig config = new ModuleConfig(name);
        for (Module module : modules.getRegistered()) {
            ValuePreset preset = ValuePreset.snapshot(name, module);
            config.presets.add(preset);
        }
        return config;
    }
}
