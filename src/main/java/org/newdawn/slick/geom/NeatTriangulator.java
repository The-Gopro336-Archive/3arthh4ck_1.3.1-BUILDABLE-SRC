/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import org.newdawn.slick.geom.Triangulator;

public class NeatTriangulator
implements Triangulator {
    static final float EPSILON = 1.0E-6f;
    private float[] pointsX = new float[100];
    private float[] pointsY = new float[100];
    private int numPoints = 0;
    private Edge[] edges = new Edge[100];
    private int[] V;
    private int numEdges = 0;
    private Triangle[] triangles = new Triangle[100];
    private int numTriangles = 0;
    private float offset = 1.0E-6f;

    public void clear() {
        this.numPoints = 0;
        this.numEdges = 0;
        this.numTriangles = 0;
    }

    private int findEdge(int i, int j) {
        int l;
        int k;
        if (i < j) {
            k = i;
            l = j;
        } else {
            k = j;
            l = i;
        }
        for (int i1 = 0; i1 < this.numEdges; ++i1) {
            if (this.edges[i1].v0 != k || this.edges[i1].v1 != l) continue;
            return i1;
        }
        return -1;
    }

    private void addEdge(int i, int j, int k) {
        int i1;
        int l;
        Edge edge;
        int k1;
        int j1;
        int l1 = this.findEdge(i, j);
        if (l1 < 0) {
            if (this.numEdges == this.edges.length) {
                Edge[] aedge = new Edge[this.edges.length * 2];
                System.arraycopy(this.edges, 0, aedge, 0, this.numEdges);
                this.edges = aedge;
            }
            j1 = -1;
            k1 = -1;
            l1 = this.numEdges++;
            edge = this.edges[l1] = new Edge();
        } else {
            edge = this.edges[l1];
            j1 = edge.t0;
            k1 = edge.t1;
        }
        if (i < j) {
            l = i;
            i1 = j;
            j1 = k;
        } else {
            l = j;
            i1 = i;
            k1 = k;
        }
        edge.v0 = l;
        edge.v1 = i1;
        edge.t0 = j1;
        edge.t1 = k1;
        edge.suspect = true;
    }

    private void deleteEdge(int i, int j) throws InternalException {
        int k = this.findEdge(i, j);
        if (0 > k) {
            throw new InternalException("Attempt to delete unknown edge");
        }
        this.edges[k] = this.edges[--this.numEdges];
    }

    void markSuspect(int i, int j, boolean flag) throws InternalException {
        int k = this.findEdge(i, j);
        if (0 > k) {
            throw new InternalException("Attempt to mark unknown edge");
        }
        this.edges[k].suspect = flag;
    }

    private Edge chooseSuspect() {
        for (int i = 0; i < this.numEdges; ++i) {
            Edge edge = this.edges[i];
            if (!edge.suspect) continue;
            edge.suspect = false;
            if (edge.t0 < 0 || edge.t1 < 0) continue;
            return edge;
        }
        return null;
    }

    private static float rho(float f, float f1, float f2, float f3, float f4, float f5) {
        float f6 = f4 - f2;
        float f9 = f1 - f5;
        float f7 = f5 - f3;
        float f8 = f - f4;
        float f18 = f6 * f9 - f7 * f8;
        if (f18 > 0.0f) {
            if (f18 < 1.0E-6f) {
                f18 = 1.0E-6f;
            }
            float f12 = f6 * f6;
            float f13 = f7 * f7;
            float f14 = f8 * f8;
            float f15 = f9 * f9;
            float f10 = f2 - f;
            float f11 = f3 - f1;
            float f16 = f10 * f10;
            float f17 = f11 * f11;
            return (f12 + f13) * (f14 + f15) * (f16 + f17) / (f18 * f18);
        }
        return -1.0f;
    }

    private static boolean insideTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
        float f8 = f4 - f2;
        float f9 = f5 - f3;
        float f10 = f - f4;
        float f11 = f1 - f5;
        float f12 = f2 - f;
        float f13 = f3 - f1;
        float f14 = f6 - f;
        float f15 = f7 - f1;
        float f16 = f6 - f2;
        float f17 = f7 - f3;
        float f18 = f6 - f4;
        float f19 = f7 - f5;
        float f22 = f8 * f17 - f9 * f16;
        float f20 = f12 * f15 - f13 * f14;
        float f21 = f10 * f19 - f11 * f18;
        return (double)f22 >= 0.0 && (double)f21 >= 0.0 && (double)f20 >= 0.0;
    }

    private boolean snip(int i, int j, int k, int l) {
        float f2 = this.pointsX[this.V[j]];
        float f = this.pointsX[this.V[i]];
        float f5 = this.pointsY[this.V[k]];
        float f1 = this.pointsY[this.V[i]];
        float f3 = this.pointsY[this.V[j]];
        float f4 = this.pointsX[this.V[k]];
        if (1.0E-6f > (f2 - f) * (f5 - f1) - (f3 - f1) * (f4 - f)) {
            return false;
        }
        for (int i1 = 0; i1 < l; ++i1) {
            float f7;
            float f6;
            if (i1 == i || i1 == j || i1 == k || !NeatTriangulator.insideTriangle(f, f1, f2, f3, f4, f5, f6 = this.pointsX[this.V[i1]], f7 = this.pointsY[this.V[i1]])) continue;
            return false;
        }
        return true;
    }

    private float area() {
        float f = 0.0f;
        int i = this.numPoints - 1;
        int j = 0;
        while (j < this.numPoints) {
            f += this.pointsX[i] * this.pointsY[j] - this.pointsY[i] * this.pointsX[j];
            i = j++;
        }
        return f * 0.5f;
    }

    public void basicTriangulation() throws InternalException {
        int i = this.numPoints;
        if (i < 3) {
            return;
        }
        this.numEdges = 0;
        this.numTriangles = 0;
        this.V = new int[i];
        if (0.0 < (double)this.area()) {
            for (int k = 0; k < i; ++k) {
                this.V[k] = k;
            }
        } else {
            for (int l = 0; l < i; ++l) {
                this.V[l] = this.numPoints - 1 - l;
            }
        }
        int k1 = 2 * i;
        int i1 = i - 1;
        while (i > 2) {
            int j1;
            if (0 >= k1--) {
                throw new InternalException("Bad polygon");
            }
            int j = i1;
            if (i <= j) {
                j = 0;
            }
            if (i <= (i1 = j + 1)) {
                i1 = 0;
            }
            if (i <= (j1 = i1 + 1)) {
                j1 = 0;
            }
            if (!this.snip(j, i1, j1, i)) continue;
            int l1 = this.V[j];
            int i2 = this.V[i1];
            int j2 = this.V[j1];
            if (this.numTriangles == this.triangles.length) {
                Triangle[] atriangle = new Triangle[this.triangles.length * 2];
                System.arraycopy(this.triangles, 0, atriangle, 0, this.numTriangles);
                this.triangles = atriangle;
            }
            this.triangles[this.numTriangles] = new Triangle(l1, i2, j2);
            this.addEdge(l1, i2, this.numTriangles);
            this.addEdge(i2, j2, this.numTriangles);
            this.addEdge(j2, l1, this.numTriangles);
            ++this.numTriangles;
            int k2 = i1;
            for (int l2 = i1 + 1; l2 < i; ++l2) {
                this.V[k2] = this.V[l2];
                ++k2;
            }
            k1 = 2 * --i;
        }
        this.V = null;
    }

    private void optimize() throws InternalException {
        Edge edge;
        while ((edge = this.chooseSuspect()) != null) {
            int i1 = edge.v0;
            int k1 = edge.v1;
            int i = edge.t0;
            int j = edge.t1;
            int j1 = -1;
            int l1 = -1;
            for (int k = 0; k < 3; ++k) {
                int i2 = this.triangles[i].v[k];
                if (i1 == i2 || k1 == i2) continue;
                l1 = i2;
                break;
            }
            for (int l = 0; l < 3; ++l) {
                int j2 = this.triangles[j].v[l];
                if (i1 == j2 || k1 == j2) continue;
                j1 = j2;
                break;
            }
            if (-1 == j1 || -1 == l1) {
                throw new InternalException("can't find quad");
            }
            float f = this.pointsX[i1];
            float f1 = this.pointsY[i1];
            float f2 = this.pointsX[j1];
            float f3 = this.pointsY[j1];
            float f4 = this.pointsX[k1];
            float f5 = this.pointsY[k1];
            float f6 = this.pointsX[l1];
            float f7 = this.pointsY[l1];
            float f8 = NeatTriangulator.rho(f, f1, f2, f3, f4, f5);
            float f9 = NeatTriangulator.rho(f, f1, f4, f5, f6, f7);
            float f10 = NeatTriangulator.rho(f2, f3, f4, f5, f6, f7);
            float f11 = NeatTriangulator.rho(f2, f3, f6, f7, f, f1);
            if (0.0f > f8 || 0.0f > f9) {
                throw new InternalException("original triangles backwards");
            }
            if (!(0.0f <= f10) || !(0.0f <= f11)) continue;
            if (f8 > f9) {
                f8 = f9;
            }
            if (f10 > f11) {
                f10 = f11;
            }
            if (!(f8 > f10)) continue;
            this.deleteEdge(i1, k1);
            this.triangles[i].v[0] = j1;
            this.triangles[i].v[1] = k1;
            this.triangles[i].v[2] = l1;
            this.triangles[j].v[0] = j1;
            this.triangles[j].v[1] = l1;
            this.triangles[j].v[2] = i1;
            this.addEdge(j1, k1, i);
            this.addEdge(k1, l1, i);
            this.addEdge(l1, j1, i);
            this.addEdge(l1, i1, j);
            this.addEdge(i1, j1, j);
            this.addEdge(j1, l1, j);
            this.markSuspect(j1, l1, false);
        }
    }

    @Override
    public boolean triangulate() {
        try {
            this.basicTriangulation();
            return true;
        }
        catch (InternalException e) {
            this.numEdges = 0;
            return false;
        }
    }

    @Override
    public void addPolyPoint(float x, float y) {
        for (int i = 0; i < this.numPoints; ++i) {
            if (this.pointsX[i] != x || this.pointsY[i] != y) continue;
            y += this.offset;
            this.offset += 1.0E-6f;
        }
        if (this.numPoints == this.pointsX.length) {
            float[] af = new float[this.numPoints * 2];
            System.arraycopy(this.pointsX, 0, af, 0, this.numPoints);
            this.pointsX = af;
            af = new float[this.numPoints * 2];
            System.arraycopy(this.pointsY, 0, af, 0, this.numPoints);
            this.pointsY = af;
        }
        this.pointsX[this.numPoints] = x;
        this.pointsY[this.numPoints] = y;
        ++this.numPoints;
    }

    @Override
    public int getTriangleCount() {
        return this.numTriangles;
    }

    @Override
    public float[] getTrianglePoint(int tri, int i) {
        float xp = this.pointsX[this.triangles[tri].v[i]];
        float yp = this.pointsY[this.triangles[tri].v[i]];
        return new float[]{xp, yp};
    }

    @Override
    public void startHole() {
    }

    class InternalException
    extends Exception {
        public InternalException(String msg) {
            super(msg);
        }
    }

    class Edge {
        int v0 = -1;
        int v1 = -1;
        int t0 = -1;
        int t1 = -1;
        boolean suspect;

        Edge() {
        }
    }

    class Triangle {
        int[] v = new int[3];

        Triangle(int i, int j, int k) {
            this.v[0] = i;
            this.v[1] = j;
            this.v[2] = k;
        }
    }
}

