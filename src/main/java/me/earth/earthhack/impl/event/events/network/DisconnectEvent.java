/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.event.events.network;

import net.minecraft.util.text.ITextComponent;

public class DisconnectEvent {
    private final ITextComponent component;

    public DisconnectEvent(ITextComponent component) {
        this.component = component;
    }

    public ITextComponent getComponent() {
        return this.component;
    }
}

