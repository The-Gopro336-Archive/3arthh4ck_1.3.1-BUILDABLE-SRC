/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.config.preset;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;

public class BuildinPreset
extends ModulePreset {
    private final Map<Setting, Object> values = new HashMap<Setting, Object>();

    public BuildinPreset(String name, Module module, String description) {
        super(name, module, description);
    }

    public <T> void add(Setting<T> setting, T value) {
        this.values.put(setting, value);
    }

    public <T> void add(String setting, T value) {
        Setting<?> s = this.getModule().getSetting(setting);
        this.add(s, value);
    }

    @Override
    public void apply() {
        for (Map.Entry<Setting, Object> entry : this.values.entrySet()) {
            entry.getKey().setValue(entry.getValue());
        }
    }
}

