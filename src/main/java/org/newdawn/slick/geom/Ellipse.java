/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import java.util.ArrayList;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.util.FastTrig;

public class Ellipse
extends Shape {
    protected static final int DEFAULT_SEGMENT_COUNT = 50;
    private int segmentCount;
    private float radius1;
    private float radius2;

    public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2) {
        this(centerPointX, centerPointY, radius1, radius2, 50);
    }

    public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount) {
        this.x = centerPointX - radius1;
        this.y = centerPointY - radius2;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.segmentCount = segmentCount;
        this.checkPoints();
    }

    public void setRadii(float radius1, float radius2) {
        this.setRadius1(radius1);
        this.setRadius2(radius2);
    }

    public float getRadius1() {
        return this.radius1;
    }

    public void setRadius1(float radius1) {
        if (radius1 != this.radius1) {
            this.radius1 = radius1;
            this.pointsDirty = true;
        }
    }

    public float getRadius2() {
        return this.radius2;
    }

    public void setRadius2(float radius2) {
        if (radius2 != this.radius2) {
            this.radius2 = radius2;
            this.pointsDirty = true;
        }
    }

    @Override
    protected void createPoints() {
        ArrayList<Float> tempPoints = new ArrayList<Float>();
        this.maxX = -1.4E-45f;
        this.maxY = -1.4E-45f;
        this.minX = Float.MAX_VALUE;
        this.minY = Float.MAX_VALUE;
        float start = 0.0f;
        float end = 359.0f;
        float cx = this.x + this.radius1;
        float cy = this.y + this.radius2;
        int step = 360 / this.segmentCount;
        for (float a = start; a <= end + (float)step; a += (float)step) {
            float ang = a;
            if (ang > end) {
                ang = end;
            }
            float newX = (float)((double)cx + FastTrig.cos(Math.toRadians(ang)) * (double)this.radius1);
            float newY = (float)((double)cy + FastTrig.sin(Math.toRadians(ang)) * (double)this.radius2);
            if (newX > this.maxX) {
                this.maxX = newX;
            }
            if (newY > this.maxY) {
                this.maxY = newY;
            }
            if (newX < this.minX) {
                this.minX = newX;
            }
            if (newY < this.minY) {
                this.minY = newY;
            }
            tempPoints.add(new Float(newX));
            tempPoints.add(new Float(newY));
        }
        this.points = new float[tempPoints.size()];
        for (int i = 0; i < this.points.length; ++i) {
            this.points[i] = ((Float)tempPoints.get(i)).floatValue();
        }
    }

    @Override
    public Shape transform(Transform transform) {
        this.checkPoints();
        Polygon resultPolygon = new Polygon();
        float[] result = new float[this.points.length];
        transform.transform(this.points, 0, result, 0, this.points.length / 2);
        resultPolygon.points = result;
        resultPolygon.checkPoints();
        return resultPolygon;
    }

    @Override
    protected void findCenter() {
        this.center = new float[2];
        this.center[0] = this.x + this.radius1;
        this.center[1] = this.y + this.radius2;
    }

    @Override
    protected void calculateRadius() {
        this.boundingCircleRadius = this.radius1 > this.radius2 ? this.radius1 : this.radius2;
    }
}

