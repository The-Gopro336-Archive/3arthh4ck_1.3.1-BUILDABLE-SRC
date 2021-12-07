/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package me.earth.earthhack.tweaker.launch.arguments;

import com.google.gson.JsonElement;
import me.earth.earthhack.tweaker.launch.arguments.AbstractArgument;

public class LongArgument
extends AbstractArgument<Long> {
    public LongArgument(Long value) {
        super(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        this.value = element.getAsLong();
    }

    @Override
    public String toJson() {
        return ((Long)this.value).toString();
    }
}

