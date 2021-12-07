/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.module.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

public abstract class AbstractData<M extends Module>
implements ModuleData {
    protected final Map<Setting<?>, String> descriptions = new HashMap();
    protected final Set<ModulePreset> presets = new LinkedHashSet<ModulePreset>();
    protected final M module;

    public AbstractData(M module) {
        this.module = module;
    }

    @Override
    public Map<Setting<?>, String> settingDescriptions() {
        return this.descriptions;
    }

    @Override
    public Collection<ModulePreset> getPresets() {
        return this.presets;
    }

    public void register(String setting, String description) {
        this.register(((SettingContainer)this.module).getSetting(setting), description);
    }

    public void register(Setting<?> setting, String description) {
        if (setting != null) {
            this.descriptions.put(setting, description);
        }
    }
}

