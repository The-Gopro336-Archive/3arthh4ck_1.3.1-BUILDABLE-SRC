/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;

final class ListenerPostKeys
extends ModuleListener<NoSlowDown, KeyboardEvent.Post> {
    public ListenerPostKeys(NoSlowDown module) {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event) {
        ((NoSlowDown)this.module).updateKeyBinds();
    }
}

