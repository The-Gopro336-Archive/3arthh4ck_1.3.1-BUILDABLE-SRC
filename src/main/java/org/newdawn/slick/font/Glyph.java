/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.font;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

public class Glyph {
    private int codePoint;
    private short width;
    private short height;
    private short yOffset;
    private boolean isMissing;
    private Shape shape;
    private Image image;

    public Glyph(int codePoint, Rectangle bounds, GlyphVector vector, int index, UnicodeFont unicodeFont) {
        int rsb;
        this.codePoint = codePoint;
        GlyphMetrics metrics = vector.getGlyphMetrics(index);
        int lsb = (int)metrics.getLSB();
        if (lsb > 0) {
            lsb = 0;
        }
        if ((rsb = (int)metrics.getRSB()) > 0) {
            rsb = 0;
        }
        int glyphWidth = bounds.width - lsb - rsb;
        int glyphHeight = bounds.height;
        if (glyphWidth > 0 && glyphHeight > 0) {
            int padTop = unicodeFont.getPaddingTop();
            int padRight = unicodeFont.getPaddingRight();
            int padBottom = unicodeFont.getPaddingBottom();
            int padLeft = unicodeFont.getPaddingLeft();
            int glyphSpacing = 1;
            this.width = (short)(glyphWidth + padLeft + padRight + glyphSpacing);
            this.height = (short)(glyphHeight + padTop + padBottom + glyphSpacing);
            this.yOffset = (short)(unicodeFont.getAscent() + bounds.y - padTop);
        }
        this.shape = vector.getGlyphOutline(index, -bounds.x + unicodeFont.getPaddingLeft(), -bounds.y + unicodeFont.getPaddingTop());
        this.isMissing = !unicodeFont.getFont().canDisplay((char)codePoint);
    }

    public int getCodePoint() {
        return this.codePoint;
    }

    public boolean isMissing() {
        return this.isMissing;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getYOffset() {
        return this.yOffset;
    }
}

