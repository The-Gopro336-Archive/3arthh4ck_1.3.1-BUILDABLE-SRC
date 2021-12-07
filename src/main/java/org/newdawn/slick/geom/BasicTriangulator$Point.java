package org.newdawn.slick.geom;

class BasicTriangulator$Point {
    private float x;
    private float y;
    private float[] array;

    public BasicTriangulator$Point(float x, float y) {
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
        if (other instanceof BasicTriangulator$Point) {
            BasicTriangulator$Point p = (BasicTriangulator$Point)other;
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }

    static float access$000(BasicTriangulator$Point x0) {
        return x0.x;
    }

    static float access$100(BasicTriangulator$Point x0) {
        return x0.y;
    }
}
