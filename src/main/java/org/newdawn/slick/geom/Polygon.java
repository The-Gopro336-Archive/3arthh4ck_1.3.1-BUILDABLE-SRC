/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import java.util.ArrayList;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

public class Polygon
extends Shape {
    private boolean allowDups = false;
    private boolean closed = true;

    public Polygon(float[] points) {
        int length = points.length;
        this.points = new float[length];
        this.maxX = -1.4E-45f;
        this.maxY = -1.4E-45f;
        this.minX = Float.MAX_VALUE;
        this.minY = Float.MAX_VALUE;
        this.x = Float.MAX_VALUE;
        this.y = Float.MAX_VALUE;
        for (int i = 0; i < length; ++i) {
            this.points[i] = points[i];
            if (i % 2 == 0) {
                if (points[i] > this.maxX) {
                    this.maxX = points[i];
                }
                if (points[i] < this.minX) {
                    this.minX = points[i];
                }
                if (!(points[i] < this.x)) continue;
                this.x = points[i];
                continue;
            }
            if (points[i] > this.maxY) {
                this.maxY = points[i];
            }
            if (points[i] < this.minY) {
                this.minY = points[i];
            }
            if (!(points[i] < this.y)) continue;
            this.y = points[i];
        }
        this.findCenter();
        this.calculateRadius();
        this.pointsDirty = true;
    }

    public Polygon() {
        this.points = new float[0];
        this.maxX = -1.4E-45f;
        this.maxY = -1.4E-45f;
        this.minX = Float.MAX_VALUE;
        this.minY = Float.MAX_VALUE;
    }

    public void setAllowDuplicatePoints(boolean allowDups) {
        this.allowDups = allowDups;
    }

    public void addPoint(float x, float y) {
        if (this.hasVertex(x, y) && !this.allowDups) {
            return;
        }
        ArrayList<Float> tempPoints = new ArrayList<Float>();
        for (int i = 0; i < this.points.length; ++i) {
            tempPoints.add(new Float(this.points[i]));
        }
        tempPoints.add(new Float(x));
        tempPoints.add(new Float(y));
        int length = tempPoints.size();
        this.points = new float[length];
        for (int i = 0; i < length; ++i) {
            this.points[i] = ((Float)tempPoints.get(i)).floatValue();
        }
        if (x > this.maxX) {
            this.maxX = x;
        }
        if (y > this.maxY) {
            this.maxY = y;
        }
        if (x < this.minX) {
            this.minX = x;
        }
        if (y < this.minY) {
            this.minY = y;
        }
        this.findCenter();
        this.calculateRadius();
        this.pointsDirty = true;
    }

    @Override
    public Shape transform(Transform transform) {
        this.checkPoints();
        Polygon resultPolygon = new Polygon();
        float[] result = new float[this.points.length];
        transform.transform(this.points, 0, result, 0, this.points.length / 2);
        resultPolygon.points = result;
        resultPolygon.findCenter();
        resultPolygon.closed = this.closed;
        return resultPolygon;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        this.pointsDirty = false;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.pointsDirty = false;
    }

    @Override
    protected void createPoints() {
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Polygon copy() {
        float[] copyPoints = new float[this.points.length];
        System.arraycopy(this.points, 0, copyPoints, 0, copyPoints.length);
        return new Polygon(copyPoints);
    }
}

