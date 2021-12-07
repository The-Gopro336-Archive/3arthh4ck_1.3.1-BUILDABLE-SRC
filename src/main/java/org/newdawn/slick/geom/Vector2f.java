/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import java.io.Serializable;
import org.newdawn.slick.util.FastTrig;

public strictfp class Vector2f
implements Serializable {
    private static final long serialVersionUID = 1339934L;
    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(float[] coords) {
        this.x = coords[0];
        this.y = coords[1];
    }

    public Vector2f(double theta) {
        this.x = 1.0f;
        this.y = 0.0f;
        this.setTheta(theta);
    }

    public void setTheta(double theta) {
        if (theta < -360.0 || theta > 360.0) {
            theta %= 360.0;
        }
        if (theta < 0.0) {
            theta = 360.0 + theta;
        }
        double oldTheta = this.getTheta();
        if (theta < -360.0 || theta > 360.0) {
            oldTheta %= 360.0;
        }
        if (theta < 0.0) {
            oldTheta = 360.0 + oldTheta;
        }
        float len = this.length();
        this.x = len * (float)FastTrig.cos(StrictMath.toRadians(theta));
        this.y = len * (float)FastTrig.sin(StrictMath.toRadians(theta));
    }

    public Vector2f add(double theta) {
        this.setTheta(this.getTheta() + theta);
        return this;
    }

    public Vector2f sub(double theta) {
        this.setTheta(this.getTheta() - theta);
        return this;
    }

    public double getTheta() {
        double theta = StrictMath.toDegrees(StrictMath.atan2(this.y, this.x));
        if (theta < -360.0 || theta > 360.0) {
            theta %= 360.0;
        }
        if (theta < 0.0) {
            theta = 360.0 + theta;
        }
        return theta;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public Vector2f(Vector2f other) {
        this(other.getX(), other.getY());
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f other) {
        this.set(other.getX(), other.getY());
    }

    public float dot(Vector2f other) {
        return this.x * other.getX() + this.y * other.getY();
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2f getPerpendicular() {
        return new Vector2f(-this.y, this.x);
    }

    public Vector2f set(float[] pt) {
        return this.set(pt[0], pt[1]);
    }

    public Vector2f negate() {
        return new Vector2f(-this.x, -this.y);
    }

    public Vector2f negateLocal() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2f add(Vector2f v) {
        this.x += v.getX();
        this.y += v.getY();
        return this;
    }

    public Vector2f sub(Vector2f v) {
        this.x -= v.getX();
        this.y -= v.getY();
        return this;
    }

    public Vector2f scale(float a) {
        this.x *= a;
        this.y *= a;
        return this;
    }

    public Vector2f normalise() {
        float l = this.length();
        if (l == 0.0f) {
            return this;
        }
        this.x /= l;
        this.y /= l;
        return this;
    }

    public Vector2f getNormal() {
        Vector2f cp = this.copy();
        cp.normalise();
        return cp;
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }

    public void projectOntoUnit(Vector2f b, Vector2f result) {
        float dp = b.dot(this);
        result.x = dp * b.getX();
        result.y = dp * b.getY();
    }

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    public String toString() {
        return "[Vector2f " + this.x + "," + this.y + " (" + this.length() + ")]";
    }

    public float distance(Vector2f other) {
        return (float)Math.sqrt(this.distanceSquared(other));
    }

    public float distanceSquared(Vector2f other) {
        float dx = other.getX() - this.getX();
        float dy = other.getY() - this.getY();
        return dx * dx + dy * dy;
    }

    public int hashCode() {
        return 997 * (int)this.x ^ 991 * (int)this.y;
    }

    public boolean equals(Object other) {
        if (other instanceof Vector2f) {
            Vector2f o = (Vector2f)other;
            return o.x == this.x && o.y == this.y;
        }
        return false;
    }
}

