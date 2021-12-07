/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.setting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

public class GeneratedSettings {
    private static final Map<SettingContainer, Set<Setting<?>>> GENERATED = new HashMap();

    private GeneratedSettings() {
        throw new AssertionError();
    }

    public static void add(SettingContainer container, Setting<?> setting) {
        GENERATED.computeIfAbsent(container, v -> new HashSet()).add(setting);
    }

    public static Set<Setting<?>> getGenerated(SettingContainer container) {
        return GENERATED.getOrDefault(container, new HashSet());
    }

    public static void clear(SettingContainer container) {
        GENERATED.remove(container);
    }
}

