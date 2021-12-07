/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding.navmesh;

import org.newdawn.slick.util.pathfinding.navmesh.Space;

public class Link {
    private float px;
    private float py;
    private Space target;

    public Link(float px, float py, Space target) {
        this.px = px;
        this.py = py;
        this.target = target;
    }

    public float distance2(float tx, float ty) {
        float dx = tx - this.px;
        float dy = ty - this.py;
        return dx * dx + dy * dy;
    }

    public float getX() {
        return this.px;
    }

    public float getY() {
        return this.py;
    }

    public Space getTarget() {
        return this.target;
    }
}

