package org.newdawn.slick.geom;

import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public strictfp class Circle
extends Ellipse {
    public float radius;

    public Circle(float centerPointX, float centerPointY, float radius) {
        this(centerPointX, centerPointY, radius, 50);
    }

    public Circle(float centerPointX, float centerPointY, float radius, int segmentCount) {
        super(centerPointX, centerPointY, radius, radius, segmentCount);
        this.x = centerPointX - radius;
        this.y = centerPointY - radius;
        this.radius = radius;
        this.boundingCircleRadius = radius;
    }

    @Override
    public float getCenterX() {
        return this.getX() + this.radius;
    }

    @Override
    public float getCenterY() {
        return this.getY() + this.radius;
    }

    @Override
    public float[] getCenter() {
        return new float[]{this.getCenterX(), this.getCenterY()};
    }

    public void setRadius(float radius) {
        if (radius != this.radius) {
            this.pointsDirty = true;
            this.radius = radius;
            this.setRadii(radius, radius);
        }
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    public boolean intersects(Shape shape) {
        if (shape instanceof Circle) {
            float dy;
            Circle other = (Circle)shape;
            float totalRad2 = this.getRadius() + other.getRadius();
            if (Math.abs(other.getCenterX() - this.getCenterX()) > totalRad2) {
                return false;
            }
            if (Math.abs(other.getCenterY() - this.getCenterY()) > totalRad2) {
                return false;
            }
            float dx = Math.abs(other.getCenterX() - this.getCenterX());
            return (totalRad2 *= totalRad2) >= dx * dx + (dy = Math.abs(other.getCenterY() - this.getCenterY())) * dy;
        }
        if (shape instanceof Rectangle) {
            return this.intersects((Rectangle)shape);
        }
        return super.intersects(shape);
    }

    @Override
    public boolean contains(float x, float y) {
        float yDelta;
        float xDelta = x - this.getCenterX();
        return xDelta * xDelta + (yDelta = y - this.getCenterY()) * yDelta < this.getRadius() * this.getRadius();
    }

    private boolean contains(Line line) {
        return this.contains(line.getX1(), line.getY1()) && this.contains(line.getX2(), line.getY2());
    }

    @Override
    protected void findCenter() {
        this.center = new float[2];
        this.center[0] = this.x + this.radius;
        this.center[1] = this.y + this.radius;
    }

    @Override
    protected void calculateRadius() {
        this.boundingCircleRadius = this.radius;
    }

    private boolean intersects(Rectangle other) {
        Rectangle box = other;
        Circle circle = this;
        if (box.contains(this.x + this.radius, this.y + this.radius)) {
            return true;
        }
        float x1 = box.getX();
        float y1 = box.getY();
        float x2 = box.getX() + box.getWidth();
        float y2 = box.getY() + box.getHeight();
        Line[] lines = new Line[]{new Line(x1, y1, x2, y1), new Line(x2, y1, x2, y2), new Line(x2, y2, x1, y2), new Line(x1, y2, x1, y1)};
        float r2 = circle.getRadius() * circle.getRadius();
        Vector2f pos = new Vector2f(circle.getCenterX(), circle.getCenterY());
        for (int i = 0; i < 4; ++i) {
            float dis = lines[i].distanceSquared(pos);
            if (!(dis < r2)) continue;
            return true;
        }
        return false;
    }

    private boolean intersects(Line other) {
        Vector2f closest;
        Vector2f lineSegmentStart = new Vector2f(other.getX1(), other.getY1());
        Vector2f lineSegmentEnd = new Vector2f(other.getX2(), other.getY2());
        Vector2f circleCenter = new Vector2f(this.getCenterX(), this.getCenterY());
        Vector2f segv = lineSegmentEnd.copy().sub(lineSegmentStart);
        Vector2f ptv = circleCenter.copy().sub(lineSegmentStart);
        float segvLength = segv.length();
        float projvl = ptv.dot(segv) / segvLength;
        if (projvl < 0.0f) {
            closest = lineSegmentStart;
        } else if (projvl > segvLength) {
            closest = lineSegmentEnd;
        } else {
            Vector2f projv = segv.copy().scale(projvl / segvLength);
            closest = lineSegmentStart.copy().add(projv);
        }
        boolean intersects = circleCenter.copy().sub(closest).lengthSquared() <= this.getRadius() * this.getRadius();
        return intersects;
    }
}
