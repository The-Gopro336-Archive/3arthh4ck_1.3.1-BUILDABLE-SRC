package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;

public class DefaultPreset
extends ModulePreset {
    public DefaultPreset(Module module) {
        super("reset", module, "Resets all settings to the default value.");
    }

    @Override
    public void apply() {
        for (Setting<?> setting : this.getModule().getSettings()) {
            setting.reset();
        }
    }
}
