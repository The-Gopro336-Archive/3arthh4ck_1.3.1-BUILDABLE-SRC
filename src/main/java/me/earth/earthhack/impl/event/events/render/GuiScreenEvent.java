/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent<T extends GuiScreen>
extends Event {
    private final T screen;

    public GuiScreenEvent(T screen) {
        this.screen = screen;
    }

    public T getScreen() {
        return this.screen;
    }
}

