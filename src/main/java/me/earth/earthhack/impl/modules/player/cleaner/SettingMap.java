/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.cleaner;

import java.util.Map;
import me.earth.earthhack.api.setting.Setting;

public class SettingMap {
    private final Setting<Integer> setting;
    private final Map<Integer, Integer> map;

    public SettingMap(Setting<Integer> setting, Map<Integer, Integer> map) {
        this.setting = setting;
        this.map = map;
    }

    public Map<Integer, Integer> getMap() {
        return this.map;
    }

    public Setting<Integer> getSetting() {
        return this.setting;
    }
}

