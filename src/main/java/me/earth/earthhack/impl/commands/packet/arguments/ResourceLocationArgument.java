/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationArgument
extends AbstractArgument<ResourceLocation> {
    public ResourceLocationArgument() {
        super(ResourceLocation.class);
    }

    @Override
    public ResourceLocation fromString(String argument) throws ArgParseException {
        return new ResourceLocation(argument);
    }
}

