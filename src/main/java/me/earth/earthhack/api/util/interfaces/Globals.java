/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package me.earth.earthhack.api.util.interfaces;

import java.util.Objects;
import net.minecraft.client.Minecraft;

public interface Globals {
    public static final Minecraft mc = Objects.requireNonNull(Minecraft.getMinecraft());
}

