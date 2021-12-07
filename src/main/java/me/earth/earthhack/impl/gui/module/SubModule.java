package me.earth.earthhack.impl.gui.module;

import me.earth.earthhack.api.module.Module;

public interface SubModule<T extends Module> {
    public T getParent();
}
