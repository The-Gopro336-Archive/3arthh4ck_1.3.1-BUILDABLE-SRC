package org.newdawn.slick.geom;

import java.io.Serializable;
import org.newdawn.slick.geom.MannTriangulator;
import org.newdawn.slick.geom.Vector2f;

class MannTriangulator$PointBag
implements Serializable {
    protected MannTriangulator.Point first;
    protected MannTriangulator$PointBag next;

    protected MannTriangulator$PointBag() {
    }

    public void clear() {
        if (this.first != null) {
            MannTriangulator.this.freePoints(this.first);
            this.first = null;
        }
    }

    public void add(MannTriangulator.Point p) {
        if (this.first != null) {
            this.first.insertBefore(p);
        } else {
            this.first = p;
            p.next = p;
            p.prev = p;
        }
    }

    public void computeAngles() {
        if (this.first == null) {
            return;
        }
        MannTriangulator.Point p = this.first;
        do {
            p.computeAngle();
        } while ((p = p.next) != this.first);
    }

    public boolean doesIntersectSegment(Vector2f v1, Vector2f v2) {
        double dxA = v2.x - v1.x;
        double dyA = v2.y - v1.y;
        MannTriangulator.Point p = this.first;
        while (true) {
            double dxB;
            double dyB;
            double d;
            MannTriangulator.Point n = p.next;
            if (p.pt != v1 && n.pt != v1 && p.pt != v2 && n.pt != v2 && Math.abs(d = dxA * (dyB = (double)(n.pt.y - p.pt.y)) - dyA * (dxB = (double)(n.pt.x - p.pt.x))) > 1.0E-5) {
                double tmp1 = p.pt.x - v1.x;
                double tmp2 = p.pt.y - v1.y;
                double tA = (dyB * tmp1 - dxB * tmp2) / d;
                double tB = (dyA * tmp1 - dxA * tmp2) / d;
                if (tA >= 0.0 && tA <= 1.0 && tB >= 0.0 && tB <= 1.0) {
                    return true;
                }
            }
            if (n == this.first) {
                return false;
            }
            p = n;
        }
    }

    public int countPoints() {
        if (this.first == null) {
            return 0;
        }
        int count = 0;
        MannTriangulator.Point p = this.first;
        do {
            ++count;
        } while ((p = p.next) != this.first);
        return count;
    }

    public boolean contains(Vector2f point) {
        if (this.first == null) {
            return false;
        }
        if (this.first.prev.pt.equals(point)) {
            return true;
        }
        return this.first.pt.equals(point);
    }
}
