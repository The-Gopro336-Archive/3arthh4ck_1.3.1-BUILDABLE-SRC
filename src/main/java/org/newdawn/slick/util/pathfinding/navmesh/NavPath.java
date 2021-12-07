/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.navmesh.Link;

public class NavPath {
    private ArrayList links = new ArrayList();

    public void push(Link link) {
        this.links.add(link);
    }

    public int length() {
        return this.links.size();
    }

    public float getX(int step) {
        return ((Link)this.links.get(step)).getX();
    }

    public float getY(int step) {
        return ((Link)this.links.get(step)).getY();
    }

    public String toString() {
        return "[Path length=" + this.length() + "]";
    }

    public void remove(int i) {
        this.links.remove(i);
    }
}

