package me.earth.earthhack.impl.gui.module;

import me.earth.earthhack.api.setting.Setting;

public class SettingHeader {
    private final String name;
    private final Setting<?> firstChild;
    private String description;

    public SettingHeader(String nameIn, Setting<?> firstChild) {
        this.name = nameIn;
        this.firstChild = firstChild;
    }

    public String getName() {
        return this.name;
    }

    public Setting<?> getSetting() {
        return this.firstChild;
    }

    public String getDescription() {
        return this.description;
    }

    public SettingHeader setDescription(String description) {
        this.description = description;
        return this;
    }
}
