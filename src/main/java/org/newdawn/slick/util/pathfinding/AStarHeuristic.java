/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding;

import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public interface AStarHeuristic {
    public float getCost(TileBasedMap var1, Mover var2, int var3, int var4, int var5, int var6);
}

