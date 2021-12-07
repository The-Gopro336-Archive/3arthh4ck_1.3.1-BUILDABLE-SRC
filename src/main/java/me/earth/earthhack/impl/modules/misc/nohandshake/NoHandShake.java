/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.nohandshake;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.modules.misc.nohandshake.ListenerCustomPayload;

public class NoHandShake
extends Module {
    public NoHandShake() {
        super("NoHandShake", Category.Misc);
        this.listeners.add(new ListenerCustomPayload(this));
    }
}

