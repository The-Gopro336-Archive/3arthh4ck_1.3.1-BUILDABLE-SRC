/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.data.MetadataSerializer
 *  net.minecraft.util.Timer
 */
package me.earth.earthhack.impl.core.ducks;

import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.Timer;

public interface IMinecraft {
    public int getRightClickDelay();

    public void setRightClickDelay(int var1);

    public Timer getTimer();

    public void click(Click var1);

    public int getGameLoop();

    public boolean isEarthhackRunning();

    public void runScheduledTasks();

    public MetadataSerializer getMetadataSerializer();

    public static enum Click {
        RIGHT,
        LEFT,
        MIDDLE;

    }
}

