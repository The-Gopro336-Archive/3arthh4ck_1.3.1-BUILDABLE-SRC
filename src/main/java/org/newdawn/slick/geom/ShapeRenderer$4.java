package org.newdawn.slick.geom;

import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.geom.Vector2f;

final class ShapeRenderer$4
implements ShapeRenderer.PointCallback {
    final ShapeFill val$fill;

    ShapeRenderer$4(ShapeFill shapeFill) {
        this.val$fill = shapeFill;
    }

    @Override
    public float[] preRenderPoint(Shape shape, float x, float y) {
        this.val$fill.colorAt(shape, x, y).bind();
        Vector2f offset = this.val$fill.getOffsetAt(shape, x, y);
        return new float[]{offset.x + x, offset.y + y};
    }
}
