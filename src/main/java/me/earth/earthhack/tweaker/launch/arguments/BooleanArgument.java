/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package me.earth.earthhack.tweaker.launch.arguments;

import com.google.gson.JsonElement;
import me.earth.earthhack.tweaker.launch.arguments.AbstractArgument;

public class BooleanArgument
extends AbstractArgument<Boolean> {
    public BooleanArgument() {
        this(Boolean.TRUE);
    }

    public BooleanArgument(Boolean initial) {
        super(initial);
    }

    @Override
    public void fromJson(JsonElement element) {
        this.value = element.getAsBoolean();
    }

    @Override
    public String toJson() {
        return ((Boolean)this.value).toString();
    }
}

