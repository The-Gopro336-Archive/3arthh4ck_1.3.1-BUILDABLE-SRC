/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.util.pathfinding.navmesh.Link;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

public class Space {
    private float x;
    private float y;
    private float width;
    private float height;
    private HashMap links = new HashMap();
    private ArrayList linksList = new ArrayList();
    private float cost;

    public Space(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void link(Space other) {
        Link link;
        if (this.inTolerance(this.x, other.x + other.width) || this.inTolerance(this.x + this.width, other.x)) {
            float linkx = this.x;
            if (this.x + this.width == other.x) {
                linkx = this.x + this.width;
            }
            float top = Math.max(this.y, other.y);
            float bottom = Math.min(this.y + this.height, other.y + other.height);
            float linky = top + (bottom - top) / 2.0f;
            link = new Link(linkx, linky, other);
            this.links.put(other, link);
            this.linksList.add(link);
        }
        if (this.inTolerance(this.y, other.y + other.height) || this.inTolerance(this.y + this.height, other.y)) {
            float linky = this.y;
            if (this.y + this.height == other.y) {
                linky = this.y + this.height;
            }
            float left = Math.max(this.x, other.x);
            float right = Math.min(this.x + this.width, other.x + other.width);
            float linkx = left + (right - left) / 2.0f;
            link = new Link(linkx, linky, other);
            this.links.put(other, link);
            this.linksList.add(link);
        }
    }

    private boolean inTolerance(float a, float b) {
        return a == b;
    }

    public boolean hasJoinedEdge(Space other) {
        if (this.inTolerance(this.x, other.x + other.width) || this.inTolerance(this.x + this.width, other.x)) {
            if (this.y >= other.y && this.y <= other.y + other.height) {
                return true;
            }
            if (this.y + this.height >= other.y && this.y + this.height <= other.y + other.height) {
                return true;
            }
            if (other.y >= this.y && other.y <= this.y + this.height) {
                return true;
            }
            if (other.y + other.height >= this.y && other.y + other.height <= this.y + this.height) {
                return true;
            }
        }
        if (this.inTolerance(this.y, other.y + other.height) || this.inTolerance(this.y + this.height, other.y)) {
            if (this.x >= other.x && this.x <= other.x + other.width) {
                return true;
            }
            if (this.x + this.width >= other.x && this.x + this.width <= other.x + other.width) {
                return true;
            }
            if (other.x >= this.x && other.x <= this.x + this.width) {
                return true;
            }
            if (other.x + other.width >= this.x && other.x + other.width <= this.x + this.width) {
                return true;
            }
        }
        return false;
    }

    public Space merge(Space other) {
        float minx = Math.min(this.x, other.x);
        float miny = Math.min(this.y, other.y);
        float newwidth = this.width + other.width;
        float newheight = this.height + other.height;
        if (this.x == other.x) {
            newwidth = this.width;
        } else {
            newheight = this.height;
        }
        return new Space(minx, miny, newwidth, newheight);
    }

    public boolean canMerge(Space other) {
        if (!this.hasJoinedEdge(other)) {
            return false;
        }
        if (this.x == other.x && this.width == other.width) {
            return true;
        }
        return this.y == other.y && this.height == other.height;
    }

    public int getLinkCount() {
        return this.linksList.size();
    }

    public Link getLink(int index) {
        return (Link)this.linksList.get(index);
    }

    public boolean contains(float xp, float yp) {
        return xp >= this.x && xp < this.x + this.width && yp >= this.y && yp < this.y + this.height;
    }

    public void fill(Space target, float sx, float sy, float cost) {
        if (cost >= this.cost) {
            return;
        }
        this.cost = cost;
        if (target == this) {
            return;
        }
        for (int i = 0; i < this.getLinkCount(); ++i) {
            Link link = this.getLink(i);
            float extraCost = link.distance2(sx, sy);
            float nextCost = cost + extraCost;
            link.getTarget().fill(target, link.getX(), link.getY(), nextCost);
        }
    }

    public void clearCost() {
        this.cost = Float.MAX_VALUE;
    }

    public float getCost() {
        return this.cost;
    }

    public boolean pickLowestCost(Space target, NavPath path) {
        if (target == this) {
            return true;
        }
        if (this.links.size() == 0) {
            return false;
        }
        Link bestLink = null;
        for (int i = 0; i < this.getLinkCount(); ++i) {
            Link link = this.getLink(i);
            if (bestLink != null && !(link.getTarget().getCost() < bestLink.getTarget().getCost())) continue;
            bestLink = link;
        }
        path.push(bestLink);
        return bestLink.getTarget().pickLowestCost(target, path);
    }

    public String toString() {
        return "[Space " + this.x + "," + this.y + " " + this.width + "," + this.height + "]";
    }
}

