package me.earth.earthhack.impl.util.render;

public class GLUProjection$Projection {
    private final double x;
    private final double y;
    private final Type t;

    public GLUProjection$Projection(double x, double y, Type t) {
        this.x = x;
        this.y = y;
        this.t = t;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Type getType() {
        return this.t;
    }

    public boolean isType(Type type) {
        return this.t == type;
    }

    public static enum Type {
        INSIDE,
        OUTSIDE,
        INVERTED,
        FAIL;

    }
}
