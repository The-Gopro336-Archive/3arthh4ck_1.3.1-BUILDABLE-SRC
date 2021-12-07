package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.TexCoordGenerator;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.svg.Gradient;

public class LinearGradientFill
implements TexCoordGenerator {
    private Vector2f start;
    private Vector2f end;
    private Gradient gradient;
    private Line line;
    private Shape shape;

    public LinearGradientFill(Shape shape, Transform trans, Gradient gradient) {
        this.gradient = gradient;
        float x = gradient.getX1();
        float y = gradient.getY1();
        float mx = gradient.getX2();
        float my = gradient.getY2();
        float h = my - y;
        float w = mx - x;
        float[] s = new float[]{x, y + h / 2.0f};
        gradient.getTransform().transform(s, 0, s, 0, 1);
        trans.transform(s, 0, s, 0, 1);
        float[] e = new float[]{x + w, y + h / 2.0f};
        gradient.getTransform().transform(e, 0, e, 0, 1);
        trans.transform(e, 0, e, 0, 1);
        this.start = new Vector2f(s[0], s[1]);
        this.end = new Vector2f(e[0], e[1]);
        this.line = new Line(this.start, this.end);
    }

    @Override
    public Vector2f getCoordFor(float x, float y) {
        Vector2f result = new Vector2f();
        this.line.getClosestPoint(new Vector2f(x, y), result);
        float u = result.distance(this.start);
        return new Vector2f(u /= this.line.length(), 0.0f);
    }
}
