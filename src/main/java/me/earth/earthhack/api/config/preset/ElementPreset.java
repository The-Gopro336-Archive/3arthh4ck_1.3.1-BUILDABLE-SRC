/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.hud.HudElement;

public abstract class ElementPreset
implements Config {
    private final HudElement element;
    private final String name;

    public ElementPreset(String name, HudElement element) {
        this.name = name;
        this.element = element;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public HudElement getElement() {
        return this.element;
    }

    public boolean equals(Object o) {
        if (o instanceof ElementPreset) {
            ElementPreset other = (ElementPreset)o;
            return other.name.equals(this.name) && other.element.equals(this.element);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

