package org.newdawn.slick.util.pathfinding;

import java.util.LinkedList;
import java.util.List;

class AStarPathFinder$PriorityList {
    private List list = new LinkedList();

    private AStarPathFinder$PriorityList() {
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
