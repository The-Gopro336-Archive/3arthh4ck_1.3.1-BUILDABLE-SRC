/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.helpers.addable.data;

import me.earth.earthhack.api.module.data.AbstractData;
import me.earth.earthhack.impl.util.helpers.addable.AddableModule;

public abstract class AddableData<T extends AddableModule>
extends AbstractData<T> {
    public AddableData(T module) {
        super(module);
        this.register(((AddableModule)module).listType, "-Whitelist: All added Elements are valid.\n-Blacklist everything added won't be used.");
    }
}

