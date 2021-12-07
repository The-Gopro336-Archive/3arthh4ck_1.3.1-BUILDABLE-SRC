/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding.heuristics;

import org.newdawn.slick.util.pathfinding.AStarHeuristic;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class ManhattanHeuristic
implements AStarHeuristic {
    private int minimumCost;

    public ManhattanHeuristic(int minimumCost) {
        this.minimumCost = minimumCost;
    }

    @Override
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        return this.minimumCost * (Math.abs(x - tx) + Math.abs(y - ty));
    }
}

