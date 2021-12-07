package org.newdawn.slick.geom;

import java.util.ArrayList;
import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Path
extends Shape {
    private ArrayList localPoints = new ArrayList();
    private float cx;
    private float cy;
    private boolean closed;
    private ArrayList holes = new ArrayList();
    private ArrayList hole;

    public Path(float sx, float sy) {
        this.localPoints.add(new float[]{sx, sy});
        this.cx = sx;
        this.cy = sy;
        this.pointsDirty = true;
    }

    public void startHole(float sx, float sy) {
        this.hole = new ArrayList();
        this.holes.add(this.hole);
    }

    public void lineTo(float x, float y) {
        if (this.hole != null) {
            this.hole.add(new float[]{x, y});
        } else {
            this.localPoints.add(new float[]{x, y});
        }
        this.cx = x;
        this.cy = y;
        this.pointsDirty = true;
    }

    public void close() {
        this.closed = true;
    }

    public void curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2) {
        this.curveTo(x, y, cx1, cy1, cx2, cy2, 10);
    }

    public void curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2, int segments) {
        if (this.cx == x && this.cy == y) {
            return;
        }
        Curve curve = new Curve(new Vector2f(this.cx, this.cy), new Vector2f(cx1, cy1), new Vector2f(cx2, cy2), new Vector2f(x, y));
        float step = 1.0f / (float)segments;
        for (int i = 1; i < segments + 1; ++i) {
            float t = (float)i * step;
            Vector2f p = curve.pointAt(t);
            if (this.hole != null) {
                this.hole.add(new float[]{p.x, p.y});
            } else {
                this.localPoints.add(new float[]{p.x, p.y});
            }
            this.cx = p.x;
            this.cy = p.y;
        }
        this.pointsDirty = true;
    }

    @Override
    protected void createPoints() {
        this.points = new float[this.localPoints.size() * 2];
        for (int i = 0; i < this.localPoints.size(); ++i) {
            float[] p = (float[])this.localPoints.get(i);
            this.points[i * 2] = p[0];
            this.points[i * 2 + 1] = p[1];
        }
    }

    @Override
    public Shape transform(Transform transform) {
        Path p = new Path(this.cx, this.cy);
        p.localPoints = this.transform(this.localPoints, transform);
        for (int i = 0; i < this.holes.size(); ++i) {
            p.holes.add(this.transform((ArrayList)this.holes.get(i), transform));
        }
        p.closed = this.closed;
        return p;
    }

    private ArrayList transform(ArrayList pts, Transform t) {
        float[] in = new float[pts.size() * 2];
        float[] out = new float[pts.size() * 2];
        for (int i = 0; i < pts.size(); ++i) {
            in[i * 2] = ((float[])pts.get(i))[0];
            in[i * 2 + 1] = ((float[])pts.get(i))[1];
        }
        t.transform(in, 0, out, 0, pts.size());
        ArrayList<float[]> outList = new ArrayList<float[]>();
        for (int i = 0; i < pts.size(); ++i) {
            outList.add(new float[]{out[i * 2], out[i * 2 + 1]});
        }
        return outList;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }
}
