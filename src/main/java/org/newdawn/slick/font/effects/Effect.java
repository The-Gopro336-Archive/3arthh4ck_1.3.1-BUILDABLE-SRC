/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.font.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;

public interface Effect {
    public void draw(BufferedImage var1, Graphics2D var2, UnicodeFont var3, Glyph var4);
}

