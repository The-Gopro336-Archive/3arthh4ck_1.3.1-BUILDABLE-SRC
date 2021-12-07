/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.setting.event;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.api.setting.Setting;

public class SettingEvent<T>
extends Event {
    private final Setting<T> setting;
    private T value;

    public SettingEvent(Setting<T> setting, T value) {
        this.setting = setting;
        this.value = value;
    }

    public Setting<T> getSetting() {
        return this.setting;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

