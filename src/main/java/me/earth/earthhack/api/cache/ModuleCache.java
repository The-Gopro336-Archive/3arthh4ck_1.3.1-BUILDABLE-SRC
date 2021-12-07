/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.cache;

import java.util.function.Supplier;
import me.earth.earthhack.api.cache.Cache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.bind.Toggle;

public class ModuleCache<T extends Module>
extends Cache<T> {
    protected Class<T> type;

    private ModuleCache() {
    }

    public ModuleCache(Register<Module> moduleManager, Class<T> type) {
        this(() -> {
            if (moduleManager != null && type != null) {
                return (Module)moduleManager.getByClass(type);
            }
            return null;
        }, type);
    }

    public ModuleCache(Supplier<T> getter, Class<T> type) {
        super(getter);
        this.type = type;
    }

    public void setModuleManager(Register<Module> moduleManager) {
        this.getter = () -> {
            if (moduleManager != null && this.type != null) {
                return (Module)moduleManager.getByClass(this.type);
            }
            return null;
        };
    }

    public boolean enable() {
        return this.computeIfPresent(Module::enable);
    }

    public boolean disable() {
        return this.computeIfPresent(Module::disable);
    }

    public boolean toggle() {
        return this.computeIfPresent(Module::toggle);
    }

    public boolean isEnabled() {
        if (this.isPresent()) {
            return ((Module)this.get()).isEnabled();
        }
        return false;
    }

    public String getDisplayInfo() {
        if (this.isPresent()) {
            return ((Module)this.get()).getDisplayInfo();
        }
        return null;
    }

    public Category getCategory() {
        if (this.isPresent()) {
            return ((Module)this.get()).getCategory();
        }
        return null;
    }

    public ModuleData getData() {
        if (this.isPresent()) {
            return ((Module)this.get()).getData();
        }
        return null;
    }

    public boolean setData(ModuleData data) {
        if (this.isPresent()) {
            ((Module)this.get()).setData(data);
            return true;
        }
        return false;
    }

    public Bind getBind() {
        if (this.isPresent()) {
            return ((Module)this.get()).getBind();
        }
        return null;
    }

    public Toggle getBindMode() {
        if (this.isPresent()) {
            return ((Module)this.get()).getBindMode();
        }
        return null;
    }

    public static ModuleCache<Module> forName(String name, Register<Module> manager) {
        NameCache cache = new NameCache(name);
        cache.setModuleManager(manager);
        return cache;
    }

    private static final class NameCache
    extends ModuleCache<Module> {
        private final String name;

        public NameCache(String name) {
            this.name = name;
            this.type = Module.class;
        }

        @Override
        public void setModuleManager(Register<Module> moduleManager) {
            this.getter = () -> {
                if (moduleManager != null) {
                    return (Module)moduleManager.getObject(this.name);
                }
                return null;
            };
        }
    }
}

