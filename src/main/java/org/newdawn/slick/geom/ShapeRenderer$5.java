package org.newdawn.slick.geom;

import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.geom.Vector2f;

final class ShapeRenderer$5
implements ShapeRenderer.PointCallback {
    final ShapeFill val$fill;
    final float[] val$center;
    final float val$scaleX;
    final float val$scaleY;
    final Image val$image;

    ShapeRenderer$5(ShapeFill shapeFill, float[] fArray, float f, float f2, Image image) {
        this.val$fill = shapeFill;
        this.val$center = fArray;
        this.val$scaleX = f;
        this.val$scaleY = f2;
        this.val$image = image;
    }

    @Override
    public float[] preRenderPoint(Shape shape, float x, float y) {
        this.val$fill.colorAt(shape, x - this.val$center[0], y - this.val$center[1]).bind();
        Vector2f offset = this.val$fill.getOffsetAt(shape, x, y);
        float tx = (x += offset.x) * this.val$scaleX;
        float ty = (y += offset.y) * this.val$scaleY;
        tx = this.val$image.getTextureOffsetX() + this.val$image.getTextureWidth() * tx;
        ty = this.val$image.getTextureOffsetY() + this.val$image.getTextureHeight() * ty;
        GL.glTexCoord2f(tx, ty);
        return new float[]{offset.x + x, offset.y + y};
    }
}
