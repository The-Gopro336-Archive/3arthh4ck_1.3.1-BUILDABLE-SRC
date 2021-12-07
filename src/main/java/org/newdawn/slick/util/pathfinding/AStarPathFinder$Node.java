package org.newdawn.slick.util.pathfinding;

class AStarPathFinder$Node
implements Comparable {
    private int x;
    private int y;
    private float cost;
    private AStarPathFinder$Node parent;
    private float heuristic;
    private int depth;
    private boolean open;
    private boolean closed;

    public AStarPathFinder$Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int setParent(AStarPathFinder$Node parent) {
        this.depth = parent.depth + 1;
        this.parent = parent;
        return this.depth;
    }

    public int compareTo(Object other) {
        AStarPathFinder$Node o = (AStarPathFinder$Node)other;
        float f = this.heuristic + this.cost;
        float of = o.heuristic + o.cost;
        if (f < of) {
            return -1;
        }
        if (f > of) {
            return 1;
        }
        return 0;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void reset() {
        this.closed = false;
        this.open = false;
        this.cost = 0.0f;
        this.depth = 0;
    }

    public String toString() {
        return "[Node " + this.x + "," + this.y + "]";
    }

    static float access$102(AStarPathFinder$Node x0, float x1) {
        x0.cost = x1;
        return x0.cost;
    }

    static int access$202(AStarPathFinder$Node x0, int x1) {
        x0.depth = x1;
        return x0.depth;
    }

    static AStarPathFinder$Node access$302(AStarPathFinder$Node x0, AStarPathFinder$Node x1) {
        x0.parent = x1;
        return x0.parent;
    }

    static int access$400(AStarPathFinder$Node x0) {
        return x0.x;
    }

    static int access$500(AStarPathFinder$Node x0) {
        return x0.y;
    }

    static int access$200(AStarPathFinder$Node x0) {
        return x0.depth;
    }

    static float access$100(AStarPathFinder$Node x0) {
        return x0.cost;
    }

    static float access$602(AStarPathFinder$Node x0, float x1) {
        x0.heuristic = x1;
        return x0.heuristic;
    }

    static AStarPathFinder$Node access$300(AStarPathFinder$Node x0) {
        return x0.parent;
    }
}
