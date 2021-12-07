/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public interface ShapeFill {
    public Color colorAt(Shape var1, float var2, float var3);

    public Vector2f getOffsetAt(Shape var1, float var2, float var3);
}

