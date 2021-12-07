package org.newdawn.slick.geom;

import java.util.ArrayList;
import org.newdawn.slick.geom.BasicTriangulator;

class BasicTriangulator$PointList {
    private ArrayList points = new ArrayList();

    public boolean contains(BasicTriangulator.Point p) {
        return this.points.contains(p);
    }

    public void add(BasicTriangulator.Point point) {
        this.points.add(point);
    }

    public void remove(BasicTriangulator.Point point) {
        this.points.remove(point);
    }

    public int size() {
        return this.points.size();
    }

    public BasicTriangulator.Point get(int i) {
        return (BasicTriangulator.Point)this.points.get(i);
    }

    public void clear() {
        this.points.clear();
    }
}
