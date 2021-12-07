/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding;

import org.newdawn.slick.util.pathfinding.PathFindingContext;

public interface TileBasedMap {
    public int getWidthInTiles();

    public int getHeightInTiles();

    public void pathFinderVisited(int var1, int var2);

    public boolean blocked(PathFindingContext var1, int var2, int var3);

    public float getCost(PathFindingContext var1, int var2, int var3);
}

