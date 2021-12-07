/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.navmesh.Link;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;
import org.newdawn.slick.util.pathfinding.navmesh.Space;

public class NavMesh {
    private ArrayList spaces = new ArrayList();

    public NavMesh() {
    }

    public NavMesh(ArrayList spaces) {
        this.spaces.addAll(spaces);
    }

    public int getSpaceCount() {
        return this.spaces.size();
    }

    public Space getSpace(int index) {
        return (Space)this.spaces.get(index);
    }

    public void addSpace(Space space) {
        this.spaces.add(space);
    }

    public Space findSpace(float x, float y) {
        for (int i = 0; i < this.spaces.size(); ++i) {
            Space space = this.getSpace(i);
            if (!space.contains(x, y)) continue;
            return space;
        }
        return null;
    }

    public NavPath findPath(float sx, float sy, float tx, float ty, boolean optimize) {
        Space source = this.findSpace(sx, sy);
        Space target = this.findSpace(tx, ty);
        if (source == null || target == null) {
            return null;
        }
        for (int i = 0; i < this.spaces.size(); ++i) {
            ((Space)this.spaces.get(i)).clearCost();
        }
        target.fill(source, tx, ty, 0.0f);
        if (target.getCost() == Float.MAX_VALUE) {
            return null;
        }
        if (source.getCost() == Float.MAX_VALUE) {
            return null;
        }
        NavPath path = new NavPath();
        path.push(new Link(sx, sy, null));
        if (source.pickLowestCost(target, path)) {
            path.push(new Link(tx, ty, null));
            if (optimize) {
                this.optimize(path);
            }
            return path;
        }
        return null;
    }

    private boolean isClear(float x1, float y1, float x2, float y2, float step) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float)Math.sqrt(dx * dx + dy * dy);
        dx *= step;
        dx /= len;
        dy *= step;
        dy /= len;
        int steps = (int)(len / step);
        for (int i = 0; i < steps; ++i) {
            float x = x1 + dx * (float)i;
            float y = y1 + dy * (float)i;
            if (this.findSpace(x, y) != null) continue;
            return false;
        }
        return true;
    }

    private void optimize(NavPath path) {
        int pt = 0;
        while (pt < path.length() - 2) {
            float ny;
            float nx;
            float sy;
            float sx = path.getX(pt);
            if (this.isClear(sx, sy = path.getY(pt), nx = path.getX(pt + 2), ny = path.getY(pt + 2), 0.1f)) {
                path.remove(pt + 1);
                continue;
            }
            ++pt;
        }
    }
}

