package me.earth.earthhack.api.cache;

import me.earth.earthhack.api.cache.Cache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

public class SettingCache<T, S extends Setting<T>, E extends SettingContainer>
extends Cache<S> {
    private final Cache<E> container;
    private T defaultValue;

    private SettingCache(Cache<E> container) {
        this.container = container;
    }

    public void setDefaultValue(T value) {
        this.defaultValue = value;
    }

    public T getValue() {
        return (T)this.returnIfPresent(Setting::getValue, this.defaultValue);
    }

    public E getContainer() {
        return (E)((SettingContainer)this.container.get());
    }

    public void setContainer(E container) {
        this.container.set(container);
    }

    public static <T, S extends Setting<T>, E extends Module> SettingCache<T, S, E> newModuleSettingCache(String name, Class<?> type, Cache<E> module, T defaultValue) {
        Class<?> converted = type;
        SettingCache<T, S, E> result = new SettingCache<T, S, E>(module);
        result.setDefaultValue(defaultValue);
        result.getter = () -> result.container.returnIfPresent(c -> c.getSetting(name, converted), null);
        return result;
    }
}
