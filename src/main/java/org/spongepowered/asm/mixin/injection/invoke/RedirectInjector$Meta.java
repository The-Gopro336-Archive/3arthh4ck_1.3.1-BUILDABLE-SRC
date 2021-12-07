package org.spongepowered.asm.mixin.injection.invoke;

import org.spongepowered.asm.mixin.injection.invoke.RedirectInjector;

class RedirectInjector$Meta {
    public static final String KEY = "redirector";
    final int priority;
    final boolean isFinal;
    final String name;
    final String desc;

    public RedirectInjector$Meta(int priority, boolean isFinal, String name, String desc) {
        this.priority = priority;
        this.isFinal = isFinal;
        this.name = name;
        this.desc = desc;
    }

    RedirectInjector getOwner() {
        return RedirectInjector.this;
    }
}
