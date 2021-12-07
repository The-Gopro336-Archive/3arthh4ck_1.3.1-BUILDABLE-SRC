/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl.renderer;

public interface LineStripRenderer {
    public boolean applyGLLineFixes();

    public void start();

    public void end();

    public void vertex(float var1, float var2);

    public void color(float var1, float var2, float var3, float var4);

    public void setWidth(float var1);

    public void setAntiAlias(boolean var1);

    public void setLineCaps(boolean var1);
}

