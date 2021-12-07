package org.newdawn.slick;

import org.newdawn.slick.Color;

public interface Renderable {
    public void draw(float var1, float var2);

    public void draw(float var1, float var2, Color var3);

    public void draw(float var1, float var2, float var3, float var4);

    public void draw(float var1, float var2, float var3, float var4, Color var5);
}
