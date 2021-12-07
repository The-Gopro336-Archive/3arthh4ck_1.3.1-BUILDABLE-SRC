package org.newdawn.slick.util.pathfinding;

import java.io.Serializable;

public class Path$Step
implements Serializable {
    private int x;
    private int y;

    public Path$Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int hashCode() {
        return this.x * this.y;
    }

    public boolean equals(Object other) {
        if (other instanceof Path$Step) {
            Path$Step o = (Path$Step)other;
            return o.x == this.x && o.y == this.y;
        }
        return false;
    }

    static int access$000(Path$Step x0) {
        return x0.x;
    }

    static int access$100(Path$Step x0) {
        return x0.y;
    }
}
