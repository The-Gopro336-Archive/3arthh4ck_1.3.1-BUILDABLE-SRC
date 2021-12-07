/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Globals;

public class WorldRenderUtil
implements Globals {
    public static void reload(boolean soft) {
        if (soft) {
            int x = (int)WorldRenderUtil.mc.player.posX;
            int y = (int)WorldRenderUtil.mc.player.posY;
            int z = (int)WorldRenderUtil.mc.player.posZ;
            int d = WorldRenderUtil.mc.gameSettings.renderDistanceChunks * 16;
            WorldRenderUtil.mc.renderGlobal.markBlockRangeForRenderUpdate(x - d, y - d, z - d, x + d, y + d, z + d);
            return;
        }
        WorldRenderUtil.mc.renderGlobal.loadRenderers();
    }
}

