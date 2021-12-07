/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.config.preset.ElementConfig;
import me.earth.earthhack.api.config.preset.ValuePreset;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.config.helpers.AbstractConfigHelper;

public class HudConfigHelper
extends AbstractConfigHelper<ElementConfig> {
    private final Register<HudElement> elements;

    public HudConfigHelper(Register<HudElement> elements) {
        super("element", "hud");
        this.elements = elements;
    }

    @Override
    protected ElementConfig create(String name) {
        return ElementConfig.create(name.toLowerCase(), this.elements);
    }

    @Override
    protected JsonObject toJson(ElementConfig config) {
        JsonObject object = new JsonObject();
        for (ValuePreset preset : config.getPresets()) {
            JsonObject presetObject = preset.toJson();
            object.add(preset.getModule().getName(), (JsonElement)presetObject);
        }
        return object;
    }

    @Override
    protected ElementConfig readFile(InputStream stream, String name) {
        JsonObject object = Jsonable.PARSER.parse((Reader)new InputStreamReader(stream)).getAsJsonObject();
        ArrayList<ValuePreset> presets = new ArrayList<ValuePreset>(object.entrySet().size());
        for (Map.Entry entry : object.entrySet()) {
            HudElement module = this.elements.getObject((String)entry.getKey());
            if (module == null) {
                Earthhack.getLogger().error("Config: Couldn't find element: " + (String)entry.getKey());
                continue;
            }
            ValuePreset preset = new ValuePreset(name, module, "A config Preset.");
            JsonObject element = ((JsonElement)entry.getValue()).getAsJsonObject();
            for (Map.Entry s : element.entrySet()) {
                boolean generated = module.getSetting((String)s.getKey()) == null;
                Setting<?> setting = module.getSettingConfig((String)s.getKey());
                if (setting == null) {
                    Earthhack.getLogger().error("Config: Couldn't find setting: " + (String)s.getKey() + " in element: " + module.getName() + ".");
                    continue;
                }
                preset.getValues().put(setting.getName(), (JsonElement)s.getValue());
                if (!generated || !GeneratedSettings.getGenerated(module).remove(setting)) continue;
                module.unregister(setting);
            }
            presets.add(preset);
        }
        ElementConfig config = new ElementConfig(name);
        config.setPresets(presets);
        return config;
    }
}

