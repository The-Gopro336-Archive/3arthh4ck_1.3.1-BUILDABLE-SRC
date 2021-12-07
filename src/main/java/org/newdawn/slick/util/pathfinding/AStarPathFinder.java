/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.newdawn.slick.util.pathfinding.AStarHeuristic;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFinder;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.heuristics.ClosestHeuristic;

public class AStarPathFinder
implements PathFinder,
PathFindingContext {
    private ArrayList closed = new ArrayList();
    private PriorityList open = new PriorityList();
    private TileBasedMap map;
    private int maxSearchDistance;
    private Node[][] nodes;
    private boolean allowDiagMovement;
    private AStarHeuristic heuristic;
    private Node current;
    private Mover mover;
    private int sourceX;
    private int sourceY;
    private int distance;

    public AStarPathFinder(TileBasedMap map, int maxSearchDistance, boolean allowDiagMovement) {
        this(map, maxSearchDistance, allowDiagMovement, new ClosestHeuristic());
    }

    public AStarPathFinder(TileBasedMap map, int maxSearchDistance, boolean allowDiagMovement, AStarHeuristic heuristic) {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;
        this.nodes = new Node[map.getWidthInTiles()][map.getHeightInTiles()];
        for (int x = 0; x < map.getWidthInTiles(); ++x) {
            for (int y = 0; y < map.getHeightInTiles(); ++y) {
                this.nodes[x][y] = new Node(x, y);
            }
        }
    }

    @Override
    public Path findPath(Mover mover, int sx, int sy, int tx, int ty) {
        this.current = null;
        this.mover = mover;
        this.sourceX = tx;
        this.sourceY = ty;
        this.distance = 0;
        if (this.map.blocked(this, tx, ty)) {
            return null;
        }
        for (int x = 0; x < this.map.getWidthInTiles(); ++x) {
            for (int y = 0; y < this.map.getHeightInTiles(); ++y) {
                this.nodes[x][y].reset();
            }
        }
        this.nodes[sx][sy].cost = 0.0f;
        this.nodes[sx][sy].depth = 0;
        this.closed.clear();
        this.open.clear();
        this.addToOpen(this.nodes[sx][sy]);
        this.nodes[tx][ty].parent = null;
        int maxDepth = 0;
        while (maxDepth < this.maxSearchDistance && this.open.size() != 0) {
            int lx = sx;
            int ly = sy;
            if (this.current != null) {
                lx = this.current.x;
                ly = this.current.y;
            }
            this.current = this.getFirstInOpen();
            this.distance = this.current.depth;
            if (this.current == this.nodes[tx][ty] && this.isValidLocation(mover, lx, ly, tx, ty)) break;
            this.removeFromOpen(this.current);
            this.addToClosed(this.current);
            for (int x = -1; x < 2; ++x) {
                for (int y = -1; y < 2; ++y) {
                    if (x == 0 && y == 0 || !this.allowDiagMovement && x != 0 && y != 0) continue;
                    int xp = x + this.current.x;
                    int yp = y + this.current.y;
                    if (!this.isValidLocation(mover, this.current.x, this.current.y, xp, yp)) continue;
                    float nextStepCost = this.current.cost + this.getMovementCost(mover, this.current.x, this.current.y, xp, yp);
                    Node neighbour = this.nodes[xp][yp];
                    this.map.pathFinderVisited(xp, yp);
                    if (nextStepCost < neighbour.cost) {
                        if (this.inOpenList(neighbour)) {
                            this.removeFromOpen(neighbour);
                        }
                        if (this.inClosedList(neighbour)) {
                            this.removeFromClosed(neighbour);
                        }
                    }
                    if (this.inOpenList(neighbour) || this.inClosedList(neighbour)) continue;
                    neighbour.cost = nextStepCost;
                    neighbour.heuristic = this.getHeuristicCost(mover, xp, yp, tx, ty);
                    maxDepth = Math.max(maxDepth, neighbour.setParent(this.current));
                    this.addToOpen(neighbour);
                }
            }
        }
        if (this.nodes[tx][ty].parent == null) {
            return null;
        }
        Path path = new Path();
        Node target = this.nodes[tx][ty];
        while (target != this.nodes[sx][sy]) {
            path.prependStep(target.x, target.y);
            target = target.parent;
        }
        path.prependStep(sx, sy);
        return path;
    }

    public int getCurrentX() {
        if (this.current == null) {
            return -1;
        }
        return this.current.x;
    }

    public int getCurrentY() {
        if (this.current == null) {
            return -1;
        }
        return this.current.y;
    }

    protected Node getFirstInOpen() {
        return (Node)this.open.first();
    }

    protected void addToOpen(Node node) {
        node.setOpen(true);
        this.open.add(node);
    }

    protected boolean inOpenList(Node node) {
        return node.isOpen();
    }

    protected void removeFromOpen(Node node) {
        node.setOpen(false);
        this.open.remove(node);
    }

    protected void addToClosed(Node node) {
        node.setClosed(true);
        this.closed.add(node);
    }

    protected boolean inClosedList(Node node) {
        return node.isClosed();
    }

    protected void removeFromClosed(Node node) {
        node.setClosed(false);
        this.closed.remove(node);
    }

    protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
        boolean invalid;
        boolean bl = invalid = x < 0 || y < 0 || x >= this.map.getWidthInTiles() || y >= this.map.getHeightInTiles();
        if (!(invalid || sx == x && sy == y)) {
            this.mover = mover;
            this.sourceX = sx;
            this.sourceY = sy;
            invalid = this.map.blocked(this, x, y);
        }
        return !invalid;
    }

    public float getMovementCost(Mover mover, int sx, int sy, int tx, int ty) {
        this.mover = mover;
        this.sourceX = sx;
        this.sourceY = sy;
        return this.map.getCost(this, tx, ty);
    }

    public float getHeuristicCost(Mover mover, int x, int y, int tx, int ty) {
        return this.heuristic.getCost(this.map, mover, x, y, tx, ty);
    }

    @Override
    public Mover getMover() {
        return this.mover;
    }

    @Override
    public int getSearchDistance() {
        return this.distance;
    }

    @Override
    public int getSourceX() {
        return this.sourceX;
    }

    @Override
    public int getSourceY() {
        return this.sourceY;
    }

    private class Node
    implements Comparable {
        private int x;
        private int y;
        private float cost;
        private Node parent;
        private float heuristic;
        private int depth;
        private boolean open;
        private boolean closed;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int setParent(Node parent) {
            this.depth = parent.depth + 1;
            this.parent = parent;
            return this.depth;
        }

        public int compareTo(Object other) {
            Node o = (Node)other;
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
    }

    private class PriorityList {
        private List list = new LinkedList();

        private PriorityList() {
        }

        public Object first() {
            return this.list.get(0);
        }

        public void clear() {
            this.list.clear();
        }

        public void add(Object o) {
            for (int i = 0; i < this.list.size(); ++i) {
                if (((Comparable)this.list.get(i)).compareTo(o) <= 0) continue;
                this.list.add(i, o);
                break;
            }
            if (!this.list.contains(o)) {
                this.list.add(o);
            }
        }

        public void remove(Object o) {
            this.list.remove(o);
        }

        public int size() {
            return this.list.size();
        }

        public boolean contains(Object o) {
            return this.list.contains(o);
        }

        public String toString() {
            String temp = "{";
            for (int i = 0; i < this.size(); ++i) {
                temp = temp + this.list.get(i).toString() + ",";
            }
            temp = temp + "}";
            return temp;
        }
    }
}

