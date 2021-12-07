package me.earth.earthhack.impl.util.render;

public class VectorUtil$Plane {
    private final double x;
    private final double y;
    private final boolean visible;

    public VectorUtil$Plane(double x, double y, boolean visible) {
        this.x = x;
        this.y = y;
        this.visible = visible;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public boolean isVisible() {
        return this.visible;
    }
}
