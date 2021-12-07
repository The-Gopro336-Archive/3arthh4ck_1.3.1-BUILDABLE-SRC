package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.Graphics2DProxy;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.text.AttributedCharacterIterator;

class HiDPIUtils$1
extends Graphics2DProxy {
    final float val$yCorrection;

    HiDPIUtils$1(Graphics2D delegate, float f) {
        this.val$yCorrection = f;
        super(delegate);
    }

    @Override
    public void drawString(String str, int x, int y) {
        super.drawString(str, (float)x, (float)y + this.val$yCorrection);
    }

    @Override
    public void drawString(String str, float x, float y) {
        super.drawString(str, x, y + this.val$yCorrection);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        super.drawString(iterator, (float)x, (float)y + this.val$yCorrection);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        super.drawString(iterator, x, y + this.val$yCorrection);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        super.drawChars(data, offset, length, x, Math.round((float)y + this.val$yCorrection));
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        super.drawBytes(data, offset, length, x, Math.round((float)y + this.val$yCorrection));
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        super.drawGlyphVector(g, x, y + this.val$yCorrection);
    }
}
