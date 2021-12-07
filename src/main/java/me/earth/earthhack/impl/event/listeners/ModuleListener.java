/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;

public abstract class ModuleListener<M, E>
extends EventListener<E>
implements Globals {
    protected final M module;

    public ModuleListener(M module, Class<? super E> target) {
        this(module, target, 10);
    }

    public ModuleListener(M module, Class<? super E> target, int priority) {
        this(module, target, priority, null);
    }

    public ModuleListener(M module, Class<? super E> target, Class<?> type) {
        this(module, target, 10, type);
    }

    public ModuleListener(M module, Class<? super E> target, int priority, Class<?> type) {
        super(target, priority, type);
        this.module = module;
    }
}

