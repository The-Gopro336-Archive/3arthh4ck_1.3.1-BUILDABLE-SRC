/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.earth.earthhack.impl.event.events.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent {
    public ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}

