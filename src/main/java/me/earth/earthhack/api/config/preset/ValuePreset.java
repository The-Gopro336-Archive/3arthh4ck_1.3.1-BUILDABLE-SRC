/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.api.config.preset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;

public class ValuePreset
extends ModulePreset {
    private final Map<String, JsonElement> values = new HashMap<String, JsonElement>();

    public ValuePreset(String name, Module module, String description) {
        super(name, module, description);
    }

    public Map<String, JsonElement> getValues() {
        return this.values;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : this.values.entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
        return object;
    }

    @Override
    public void apply() {
        Module module = this.getModule();
        Set<Setting<?>> generated = GeneratedSettings.getGenerated(module);
        for (Setting<?> setting : generated) {
            module.unregister(setting);
        }
        GeneratedSettings.clear(module);
        Map.Entry<String, JsonElement> enabled = null;
        for (Map.Entry<String, JsonElement> entry : this.values.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Enabled")) {
                enabled = entry;
                continue;
            }
            this.setSetting(module, entry);
        }
        if (enabled != null) {
            this.setSetting(module, enabled);
        }
    }

    public static ValuePreset snapshot(String name, Module module) {
        ValuePreset preset = new ValuePreset(name, module, "A config Preset.");
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BindSetting) continue;
            JsonElement element = Jsonable.parse(setting.toJson());
            preset.getValues().put(setting.getName(), element);
        }
        return preset;
    }

    protected void setSetting(Module module, Map.Entry<String, JsonElement> entry) {
        Setting<?> setting = module.getSettingConfig(entry.getKey());
        if (setting != null) {
            try {
                setting.fromJson(entry.getValue());
            }
            catch (Exception e) {
                System.out.println(module.getName() + " : " + setting.getName() + " : Couldn't set value from json:");
                e.printStackTrace();
            }
        }
    }
}

