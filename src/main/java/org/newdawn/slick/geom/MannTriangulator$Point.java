package org.newdawn.slick.geom;

import java.io.Serializable;
import org.newdawn.slick.geom.Vector2f;

class MannTriangulator$Point
implements Serializable {
    protected Vector2f pt;
    protected MannTriangulator$Point prev;
    protected MannTriangulator$Point next;
    protected double nx;
    protected double ny;
    protected double angle;
    protected double dist;

    public MannTriangulator$Point(Vector2f pt) {
        this.pt = pt;
    }

    public void unlink() {
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
    }

    public void insertBefore(MannTriangulator$Point p) {
        this.prev.next = p;
        p.prev = this.prev;
        p.next = this;
        this.prev = p;
    }

    public void insertAfter(MannTriangulator$Point p) {
        this.next.prev = p;
        p.prev = this;
        p.next = this.next;
        this.next = p;
    }

    private double hypot(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public void computeAngle() {
        if (this.prev.pt.equals(this.pt)) {
            this.pt.x += 0.01f;
        }
        double dx1 = this.pt.x - this.prev.pt.x;
        double dy1 = this.pt.y - this.prev.pt.y;
        double len1 = this.hypot(dx1, dy1);
        dx1 /= len1;
        dy1 /= len1;
        if (this.next.pt.equals(this.pt)) {
            this.pt.y += 0.01f;
        }
        double dx2 = this.next.pt.x - this.pt.x;
        double dy2 = this.next.pt.y - this.pt.y;
        double len2 = this.hypot(dx2, dy2);
        double nx1 = -dy1;
        double ny1 = dx1;
        this.nx = (nx1 - (dy2 /= len2)) * 0.5;
        this.ny = (ny1 + (dx2 /= len2)) * 0.5;
        if (this.nx * this.nx + this.ny * this.ny < 1.0E-5) {
            this.nx = dx1;
            this.ny = dy2;
            this.angle = 1.0;
            if (dx1 * dx2 + dy1 * dy2 > 0.0) {
                this.nx = -dx1;
                this.ny = -dy1;
            }
        } else {
            this.angle = this.nx * dx2 + this.ny * dy2;
        }
    }

    public double getAngle(MannTriangulator$Point p) {
        double dx = p.pt.x - this.pt.x;
        double dy = p.pt.y - this.pt.y;
        double dlen = this.hypot(dx, dy);
        return (this.nx * dx + this.ny * dy) / dlen;
    }

    public boolean isConcave() {
        return this.angle < 0.0;
    }

    public boolean isInfront(double dx, double dy) {
        boolean sidePrev = (double)(this.prev.pt.y - this.pt.y) * dx + (double)(this.pt.x - this.prev.pt.x) * dy >= 0.0;
        boolean sideNext = (double)(this.pt.y - this.next.pt.y) * dx + (double)(this.next.pt.x - this.pt.x) * dy >= 0.0;
        return this.angle < 0.0 ? sidePrev | sideNext : sidePrev & sideNext;
    }

    public boolean isInfront(MannTriangulator$Point p) {
        return this.isInfront(p.pt.x - this.pt.x, p.pt.y - this.pt.y);
    }
}
