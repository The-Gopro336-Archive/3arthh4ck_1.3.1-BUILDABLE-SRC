/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;

public class Caches {
    private static final Map<Class<? extends Module>, ModuleCache<? extends Module>> MODULES = new ConcurrentHashMap<Class<? extends Module>, ModuleCache<? extends Module>>();
    private static final Map<Class<?>, Map<SettingType<?>, SettingCache<?, ?, ?>>> SETTINGS = new ConcurrentHashMap();
    private static Register<Module> moduleManager;

    public static <M extends Module> ModuleCache<M> getModule(Class<M> clazz) {
        return MODULES.computeIfAbsent(clazz, v -> new ModuleCache(moduleManager, clazz));
    }

    public static <T, S extends Setting<T>, E extends Module> SettingCache<T, S, E> getSetting(Class<E> module, Class<?> settingType, String setting, T defaultValue) {
        Class<?> converted = settingType;
        Map inner = SETTINGS.computeIfAbsent(module, v -> new ConcurrentHashMap());
        SettingType type = new SettingType(setting, converted);
        return inner.computeIfAbsent(type, v -> {
            ModuleCache moduleCache = Caches.getModule(module);
            return SettingCache.newModuleSettingCache(setting, converted, moduleCache, defaultValue);
        });
    }

    public static void setManager(Register<Module> moduleManagerIn) {
        moduleManager = moduleManagerIn;
        for (Map.Entry<Class<? extends Module>, ModuleCache<? extends Module>> entry : MODULES.entrySet()) {
            entry.getValue().setModuleManager(moduleManagerIn);
            entry.getValue().setFrozen(false);
            if (entry.getValue().isPresent()) continue;
            Earthhack.getLogger().error("Module-Caches: couldn't make " + entry.getKey().getName() + " present.");
            entry.getValue().setFrozen(true);
        }
        for (Map.Entry<Class<Module>, Object> entry : SETTINGS.entrySet()) {
            if (entry.getValue() == null) continue;
            for (Map.Entry entry1 : ((Map)entry.getValue()).entrySet()) {
                ((SettingCache)entry1.getValue()).setFrozen(false);
                if (((SettingCache)entry1.getValue()).isPresent()) continue;
                Earthhack.getLogger().error("Setting-Caches: couldn't make " + entry.getKey().getName() + " - " + ((SettingType)entry1.getKey()).getName() + " (" + ((SettingType)entry1.getKey()).getType().getName() + ") present.");
                ((SettingCache)entry1.getValue()).setFrozen(true);
            }
        }
    }

    private static final class SettingType<S extends Setting<?>> {
        private final Class<S> type;
        private final String name;

        public SettingType(String name, Class<S> type) {
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
            if (o instanceof SettingType) {
                return ((SettingType)o).type == this.type && this.name.equals(((SettingType)o).name);
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }
}

