package org.newdawn.slick.geom;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.geom.TexCoordGenerator;
import org.newdawn.slick.geom.Vector2f;

final class ShapeRenderer$6
implements ShapeRenderer.PointCallback {
    final TexCoordGenerator val$gen;

    ShapeRenderer$6(TexCoordGenerator texCoordGenerator) {
        this.val$gen = texCoordGenerator;
    }

    @Override
    public float[] preRenderPoint(Shape shape, float x, float y) {
        Vector2f tex = this.val$gen.getCoordFor(x, y);
        GL.glTexCoord2f(tex.x, tex.y);
        return new float[]{x, y};
    }
}
