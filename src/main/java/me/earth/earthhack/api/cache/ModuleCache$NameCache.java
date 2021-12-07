package me.earth.earthhack.api.cache;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;

final class ModuleCache$NameCache
extends ModuleCache<Module> {
    private final String name;

    public ModuleCache$NameCache(String name) {
        super(null);
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
