/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Line
extends Shape {
    private Vector2f start;
    private Vector2f end;
    private Vector2f vec;
    private float lenSquared;
    private Vector2f loc = new Vector2f(0.0f, 0.0f);
    private Vector2f v = new Vector2f(0.0f, 0.0f);
    private Vector2f v2 = new Vector2f(0.0f, 0.0f);
    private Vector2f proj = new Vector2f(0.0f, 0.0f);
    private Vector2f closest = new Vector2f(0.0f, 0.0f);
    private Vector2f other = new Vector2f(0.0f, 0.0f);
    private boolean outerEdge = true;
    private boolean innerEdge = true;

    public Line(float x, float y, boolean inner, boolean outer) {
        this(0.0f, 0.0f, x, y);
    }

    public Line(float x, float y) {
        this(x, y, true, true);
    }

    public Line(float x1, float y1, float x2, float y2) {
        this(new Vector2f(x1, y1), new Vector2f(x2, y2));
    }

    public Line(float x1, float y1, float dx, float dy, boolean dummy) {
        this(new Vector2f(x1, y1), new Vector2f(x1 + dx, y1 + dy));
    }

    public Line(float[] start, float[] end) {
        this.set(start, end);
    }

    public Line(Vector2f start, Vector2f end) {
        this.set(start, end);
    }

    public void set(float[] start, float[] end) {
        this.set(start[0], start[1], end[0], end[1]);
    }

    public Vector2f getStart() {
        return this.start;
    }

    public Vector2f getEnd() {
        return this.end;
    }

    public float length() {
        return this.vec.length();
    }

    public float lengthSquared() {
        return this.vec.lengthSquared();
    }

    public void set(Vector2f start, Vector2f end) {
        this.pointsDirty = true;
        if (this.start == null) {
            this.start = new Vector2f();
        }
        this.start.set(start);
        if (this.end == null) {
            this.end = new Vector2f();
        }
        this.end.set(end);
        this.vec = new Vector2f(end);
        this.vec.sub(start);
        this.lenSquared = this.vec.lengthSquared();
    }

    public void set(float sx, float sy, float ex, float ey) {
        this.pointsDirty = true;
        this.start.set(sx, sy);
        this.end.set(ex, ey);
        float dx = ex - sx;
        float dy = ey - sy;
        this.vec.set(dx, dy);
        this.lenSquared = dx * dx + dy * dy;
    }

    public float getDX() {
        return this.end.getX() - this.start.getX();
    }

    public float getDY() {
        return this.end.getY() - this.start.getY();
    }

    @Override
    public float getX() {
        return this.getX1();
    }

    @Override
    public float getY() {
        return this.getY1();
    }

    public float getX1() {
        return this.start.getX();
    }

    public float getY1() {
        return this.start.getY();
    }

    public float getX2() {
        return this.end.getX();
    }

    public float getY2() {
        return this.end.getY();
    }

    public float distance(Vector2f point) {
        return (float)Math.sqrt(this.distanceSquared(point));
    }

    public boolean on(Vector2f point) {
        this.getClosestPoint(point, this.closest);
        return point.equals(this.closest);
    }

    public float distanceSquared(Vector2f point) {
        this.getClosestPoint(point, this.closest);
        this.closest.sub(point);
        float result = this.closest.lengthSquared();
        return result;
    }

    public void getClosestPoint(Vector2f point, Vector2f result) {
        this.loc.set(point);
        this.loc.sub(this.start);
        float projDistance = this.vec.dot(this.loc);
        projDistance /= this.vec.lengthSquared();
        if (projDistance < 0.0f) {
            result.set(this.start);
            return;
        }
        if (projDistance > 1.0f) {
            result.set(this.end);
            return;
        }
        result.x = this.start.getX() + projDistance * this.vec.getX();
        result.y = this.start.getY() + projDistance * this.vec.getY();
    }

    public String toString() {
        return "[Line " + this.start + "," + this.end + "]";
    }

    public Vector2f intersect(Line other) {
        return this.intersect(other, false);
    }

    public Vector2f intersect(Line other, boolean limit) {
        Vector2f temp = new Vector2f();
        if (!this.intersect(other, limit, temp)) {
            return null;
        }
        return temp;
    }

    public boolean intersect(Line other, boolean limit, Vector2f result) {
        float dx1 = this.end.getX() - this.start.getX();
        float dx2 = other.end.getX() - other.start.getX();
        float dy1 = this.end.getY() - this.start.getY();
        float dy2 = other.end.getY() - other.start.getY();
        float denom = dy2 * dx1 - dx2 * dy1;
        if (denom == 0.0f) {
            return false;
        }
        float ua = dx2 * (this.start.getY() - other.start.getY()) - dy2 * (this.start.getX() - other.start.getX());
        ua /= denom;
        float ub = dx1 * (this.start.getY() - other.start.getY()) - dy1 * (this.start.getX() - other.start.getX());
        ub /= denom;
        if (limit && (ua < 0.0f || ua > 1.0f || ub < 0.0f || ub > 1.0f)) {
            return false;
        }
        float u = ua;
        float ix = this.start.getX() + u * (this.end.getX() - this.start.getX());
        float iy = this.start.getY() + u * (this.end.getY() - this.start.getY());
        result.set(ix, iy);
        return true;
    }

    @Override
    protected void createPoints() {
        this.points = new float[4];
        this.points[0] = this.getX1();
        this.points[1] = this.getY1();
        this.points[2] = this.getX2();
        this.points[3] = this.getY2();
    }

    @Override
    public Shape transform(Transform transform) {
        float[] temp = new float[4];
        this.createPoints();
        transform.transform(this.points, 0, temp, 0, 2);
        return new Line(temp[0], temp[1], temp[2], temp[3]);
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public boolean intersects(Shape shape) {
        if (shape instanceof Circle) {
            return shape.intersects(this);
        }
        return super.intersects(shape);
    }
}

