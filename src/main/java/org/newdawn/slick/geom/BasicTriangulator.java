/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import java.util.ArrayList;
import org.newdawn.slick.geom.Triangulator;

public class BasicTriangulator
implements Triangulator {
    private static final float EPSILON = 1.0E-10f;
    private PointList poly = new PointList();
    private PointList tris = new PointList();
    private boolean tried;

    @Override
    public void addPolyPoint(float x, float y) {
        Point p = new Point(x, y);
        if (!this.poly.contains(p)) {
            this.poly.add(p);
        }
    }

    public int getPolyPointCount() {
        return this.poly.size();
    }

    public float[] getPolyPoint(int index) {
        return new float[]{this.poly.get(index).x, this.poly.get(index).y};
    }

    @Override
    public boolean triangulate() {
        this.tried = true;
        boolean worked = this.process(this.poly, this.tris);
        return worked;
    }

    @Override
    public int getTriangleCount() {
        if (!this.tried) {
            throw new RuntimeException("Call triangulate() before accessing triangles");
        }
        return this.tris.size() / 3;
    }

    @Override
    public float[] getTrianglePoint(int tri, int i) {
        if (!this.tried) {
            throw new RuntimeException("Call triangulate() before accessing triangles");
        }
        return this.tris.get(tri * 3 + i).toArray();
    }

    private float area(PointList contour) {
        int n = contour.size();
        float A = 0.0f;
        int p = n - 1;
        int q = 0;
        while (q < n) {
            Point contourP = contour.get(p);
            Point contourQ = contour.get(q);
            A += contourP.getX() * contourQ.getY() - contourQ.getX() * contourP.getY();
            p = q++;
        }
        return A * 0.5f;
    }

    private boolean insideTriangle(float Ax, float Ay, float Bx, float By, float Cx, float Cy, float Px, float Py) {
        float ax = Cx - Bx;
        float ay = Cy - By;
        float bx = Ax - Cx;
        float by = Ay - Cy;
        float cx = Bx - Ax;
        float cy = By - Ay;
        float apx = Px - Ax;
        float apy = Py - Ay;
        float bpx = Px - Bx;
        float bpy = Py - By;
        float cpx = Px - Cx;
        float cpy = Py - Cy;
        float aCROSSbp = ax * bpy - ay * bpx;
        float cCROSSap = cx * apy - cy * apx;
        float bCROSScp = bx * cpy - by * cpx;
        return aCROSSbp >= 0.0f && bCROSScp >= 0.0f && cCROSSap >= 0.0f;
    }

    private boolean snip(PointList contour, int u, int v, int w, int n, int[] V) {
        float Ax = contour.get(V[u]).getX();
        float Ay = contour.get(V[u]).getY();
        float Bx = contour.get(V[v]).getX();
        float By = contour.get(V[v]).getY();
        float Cx = contour.get(V[w]).getX();
        float Cy = contour.get(V[w]).getY();
        if (1.0E-10f > (Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)) {
            return false;
        }
        for (int p = 0; p < n; ++p) {
            float Py;
            float Px;
            if (p == u || p == v || p == w || !this.insideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px = contour.get(V[p]).getX(), Py = contour.get(V[p]).getY())) continue;
            return false;
        }
        return true;
    }

    private boolean process(PointList contour, PointList result) {
        int v;
        result.clear();
        int n = contour.size();
        if (n < 3) {
            return false;
        }
        int[] V = new int[n];
        if (0.0f < this.area(contour)) {
            for (v = 0; v < n; ++v) {
                V[v] = v;
            }
        } else {
            for (v = 0; v < n; ++v) {
                V[v] = n - 1 - v;
            }
        }
        int nv = n;
        int count = 2 * nv;
        int m = 0;
        int v2 = nv - 1;
        while (nv > 2) {
            int w;
            if (0 >= count--) {
                return false;
            }
            int u = v2;
            if (nv <= u) {
                u = 0;
            }
            if (nv <= (v2 = u + 1)) {
                v2 = 0;
            }
            if (nv <= (w = v2 + 1)) {
                w = 0;
            }
            if (!this.snip(contour, u, v2, w, nv, V)) continue;
            int a = V[u];
            int b = V[v2];
            int c = V[w];
            result.add(contour.get(a));
            result.add(contour.get(b));
            result.add(contour.get(c));
            ++m;
            int s = v2;
            for (int t = v2 + 1; t < nv; ++t) {
                V[s] = V[t];
                ++s;
            }
            count = 2 * --nv;
        }
        return true;
    }

    @Override
    public void startHole() {
    }

    private class PointList {
        private ArrayList points = new ArrayList();

        public boolean contains(Point p) {
            return this.points.contains(p);
        }

        public void add(Point point) {
            this.points.add(point);
        }

        public void remove(Point point) {
            this.points.remove(point);
        }

        public int size() {
            return this.points.size();
        }

        public Point get(int i) {
            return (Point)this.points.get(i);
        }

        public void clear() {
            this.points.clear();
        }
    }

    private class Point {
        private float x;
        private float y;
        private float[] array;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
            this.array = new float[]{x, y};
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float[] toArray() {
            return this.array;
        }

        public int hashCode() {
            return (int)(this.x * this.y * 31.0f);
        }

        public boolean equals(Object other) {
            if (other instanceof Point) {
                Point p = (Point)other;
                return p.x == this.x && p.y == this.y;
            }
            return false;
        }
    }
}

