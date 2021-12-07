package me.earth.earthhack.api.setting;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import me.earth.earthhack.api.setting.Setting;

public class SettingContainer {
    private Map<String, Setting<?>> settings = new LinkedHashMap();

    public <T, S extends Setting<T>> S register(S setting) {
        if (setting != null) {
            setting.setContainer(this);
            this.settings.put(setting.getName().toLowerCase(), setting);
            return setting;
        }
        return null;
    }

    public Setting<?> unregister(Setting<?> setting) {
        return setting == null ? null : this.settings.remove(setting.getName().toLowerCase());
    }

    public final Setting<?> getSetting(String name) {
        return this.settings.get(name.toLowerCase());
    }

    public <T, S extends Setting<T>> S getSetting(String name, Class<?> clazz) {
        Setting<?> setting = this.settings.get(name.toLowerCase());
        if (clazz.isInstance(setting)) {
            return (S)setting;
        }
        return null;
    }

    public <T, S extends Setting<T>> S getSetting(String name, Class<S> clazz, Class<T> type) {
        Setting<?> setting = this.settings.get(name.toLowerCase());
        if (clazz.isInstance(setting) && setting.getInitial().getClass() == type) {
            return (S)setting;
        }
        return null;
    }

    public Setting<?> getSettingConfig(String name) {
        return this.settings.get(name.toLowerCase());
    }

    public Collection<Setting<?>> getSettings() {
        return Collections.unmodifiableCollection(this.settings.values());
    }

    public <T, S extends Setting<T>> S registerBefore(S setting, Setting<?> before) {
        return this.registerAt(setting, before, true);
    }

    public <T, S extends Setting<T>> S registerAfter(S setting, Setting<?> after) {
        return this.registerAt(setting, after, false);
    }

    private <T, S extends Setting<T>> S registerAt(S setting, Setting<?> target, boolean before) {
        if (setting != null) {
            setting.setContainer(this);
            LinkedHashMap newSettings = new LinkedHashMap();
            for (Map.Entry<String, Setting<?>> entry : this.settings.entrySet()) {
                boolean found = entry.getValue().equals(target);
                if (found && before) {
                    newSettings.put(setting.getName().toLowerCase(), setting);
                }
                newSettings.put(entry.getKey(), entry.getValue());
                if (!found || before) continue;
                newSettings.put(setting.getName().toLowerCase(), setting);
            }
            this.settings = newSettings;
            return setting;
        }
        return null;
    }
}
