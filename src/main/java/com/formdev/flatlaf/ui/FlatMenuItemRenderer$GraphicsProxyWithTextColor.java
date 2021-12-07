package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.Graphics2DProxy;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.text.AttributedCharacterIterator;

class FlatMenuItemRenderer$GraphicsProxyWithTextColor
extends Graphics2DProxy {
    private final Color textColor;

    FlatMenuItemRenderer$GraphicsProxyWithTextColor(Graphics2D delegate, Color textColor) {
        super(delegate);
        this.textColor = textColor;
    }

    @Override
    public void drawString(String str, int x, int y) {
        Paint oldPaint = this.getPaint();
        this.setPaint(this.textColor);
        super.drawString(str, x, y);
        this.setPaint(oldPaint);
    }

    @Override
    public void drawString(String str, float x, float y) {
        Paint oldPaint = this.getPaint();
        this.setPaint(this.textColor);
        super.drawString(str, x, y);
        this.setPaint(oldPaint);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        Paint oldPaint = this.getPaint();
        this.setPaint(this.textColor);
        super.drawString(iterator, x, y);
        this.setPaint(oldPaint);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        Paint oldPaint = this.getPaint();
        this.setPaint(this.textColor);
        super.drawString(iterator, x, y);
        this.setPaint(oldPaint);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        Paint oldPaint = this.getPaint();
        this.setPaint(this.textColor);
        super.drawChars(data, offset, length, x, y);
        this.setPaint(oldPaint);
    }
}
