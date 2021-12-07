package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.config.preset.ModuleConfig;
import me.earth.earthhack.api.config.preset.ValuePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.config.helpers.AbstractConfigHelper;

public class ModuleConfigHelper
extends AbstractConfigHelper<ModuleConfig> {
    private final Register<Module> modules;

    public ModuleConfigHelper(Register<Module> mods) {
        super("module", "modules");
        this.modules = mods;
    }

    @Override
    protected ModuleConfig create(String name) {
        return ModuleConfig.create(name.toLowerCase(), this.modules);
    }

    @Override
    protected JsonObject toJson(ModuleConfig config) {
        JsonObject object = new JsonObject();
        for (ValuePreset preset : config.getPresets()) {
            JsonObject presetObject = preset.toJson();
            object.add(preset.getModule().getName(), presetObject);
        }
        return object;
    }

    @Override
    protected ModuleConfig readFile(InputStream stream, String name) {
        JsonObject object = Jsonable.PARSER.parse(new InputStreamReader(stream)).getAsJsonObject();
        ArrayList<ValuePreset> presets = new ArrayList<ValuePreset>(object.entrySet().size());
        for (Map.Entry entry : object.entrySet()) {
            Module module = this.modules.getObject((String)entry.getKey());
            if (module == null) {
                Earthhack.getLogger().error("Config: Couldn't find module: " + (String)entry.getKey());
                continue;
            }
            ValuePreset preset = new ValuePreset(name, module, "A config Preset.");
            JsonObject element = ((JsonElement)entry.getValue()).getAsJsonObject();
            for (Map.Entry s : element.entrySet()) {
                boolean generated = module.getSetting((String)s.getKey()) == null;
                Setting<?> setting = module.getSettingConfig((String)s.getKey());
                if (setting == null) {
                    Earthhack.getLogger().error("Config: Couldn't find setting: " + (String)s.getKey() + " in module: " + module.getName() + ".");
                    continue;
                }
                preset.getValues().put(setting.getName(), (JsonElement)s.getValue());
                if (!generated || !GeneratedSettings.getGenerated(module).remove(setting)) continue;
                module.unregister(setting);
            }
            presets.add(preset);
        }
        ModuleConfig config = new ModuleConfig(name);
        config.setPresets(presets);
        return config;
    }
}
