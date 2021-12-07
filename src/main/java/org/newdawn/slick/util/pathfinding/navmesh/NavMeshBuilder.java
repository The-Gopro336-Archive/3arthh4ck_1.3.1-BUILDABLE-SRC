package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.Space;

public class NavMeshBuilder
implements PathFindingContext {
    private int sx;
    private int sy;
    private float smallestSpace = 0.2f;
    private boolean tileBased;

    public NavMesh build(TileBasedMap map) {
        return this.build(map, true);
    }

    public NavMesh build(TileBasedMap map, boolean tileBased) {
        this.tileBased = tileBased;
        ArrayList<Space> spaces = new ArrayList<Space>();
        if (tileBased) {
            for (int x = 0; x < map.getWidthInTiles(); ++x) {
                for (int y = 0; y < map.getHeightInTiles(); ++y) {
                    if (map.blocked(this, x, y)) continue;
                    spaces.add(new Space(x, y, 1.0f, 1.0f));
                }
            }
        } else {
            Space space = new Space(0.0f, 0.0f, map.getWidthInTiles(), map.getHeightInTiles());
            this.subsection(map, space, spaces);
        }
        while (this.mergeSpaces(spaces)) {
        }
        this.linkSpaces(spaces);
        return new NavMesh(spaces);
    }

    private boolean mergeSpaces(ArrayList spaces) {
        for (int source = 0; source < spaces.size(); ++source) {
            Space a = (Space)spaces.get(source);
            for (int target = source + 1; target < spaces.size(); ++target) {
                Space b = (Space)spaces.get(target);
                if (!a.canMerge(b)) continue;
                spaces.remove(a);
                spaces.remove(b);
                spaces.add(a.merge(b));
                return true;
            }
        }
        return false;
    }

    private void linkSpaces(ArrayList spaces) {
        for (int source = 0; source < spaces.size(); ++source) {
            Space a = (Space)spaces.get(source);
            for (int target = source + 1; target < spaces.size(); ++target) {
                Space b = (Space)spaces.get(target);
                if (!a.hasJoinedEdge(b)) continue;
                a.link(b);
                b.link(a);
            }
        }
    }

    public boolean clear(TileBasedMap map, Space space) {
        if (this.tileBased) {
            return true;
        }
        float x = 0.0f;
        boolean donex = false;
        while (x < space.getWidth()) {
            float y = 0.0f;
            boolean doney = false;
            while (y < space.getHeight()) {
                this.sx = (int)(space.getX() + x);
                this.sy = (int)(space.getY() + y);
                if (map.blocked(this, this.sx, this.sy)) {
                    return false;
                }
                if (!((y += 0.1f) > space.getHeight()) || doney) continue;
                y = space.getHeight();
                doney = true;
            }
            if (!((x += 0.1f) > space.getWidth()) || donex) continue;
            x = space.getWidth();
            donex = true;
        }
        return true;
    }

    private void subsection(TileBasedMap map, Space space, ArrayList spaces) {
        if (!this.clear(map, space)) {
            float width2 = space.getWidth() / 2.0f;
            float height2 = space.getHeight() / 2.0f;
            if (width2 < this.smallestSpace && height2 < this.smallestSpace) {
                return;
            }
            this.subsection(map, new Space(space.getX(), space.getY(), width2, height2), spaces);
            this.subsection(map, new Space(space.getX(), space.getY() + height2, width2, height2), spaces);
            this.subsection(map, new Space(space.getX() + width2, space.getY(), width2, height2), spaces);
            this.subsection(map, new Space(space.getX() + width2, space.getY() + height2, width2, height2), spaces);
        } else {
            spaces.add(space);
        }
    }

    @Override
    public Mover getMover() {
        return null;
    }

    @Override
    public int getSearchDistance() {
        return 0;
    }

    @Override
    public int getSourceX() {
        return this.sx;
    }

    @Override
    public int getSourceY() {
        return this.sy;
    }
}
