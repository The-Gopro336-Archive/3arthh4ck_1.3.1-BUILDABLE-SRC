package me.earth.earthhack.impl.modules;

import me.earth.earthhack.api.setting.Setting;

final class Caches$SettingType<S extends Setting<?>> {
    private final Class<S> type;
    private final String name;

    public Caches$SettingType(String name, Class<S> type) {
        this.name = name;
        this.type = type;
    }

    public Class<S> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (o instanceof Caches$SettingType) {
            return ((Caches$SettingType)o).type == this.type && this.name.equals(((Caches$SettingType)o).name);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
