package org.newdawn.slick.util.pathfinding.heuristics;

import org.newdawn.slick.util.pathfinding.AStarHeuristic;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class ClosestSquaredHeuristic
implements AStarHeuristic {
    @Override
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        float dx = tx - x;
        float dy = ty - y;
        return dx * dx + dy * dy;
    }
}
