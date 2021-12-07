package org.newdawn.slick;

import java.io.UnsupportedEncodingException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

public class SpriteSheetFont
implements Font {
    private SpriteSheet font;
    private char startingCharacter;
    private int charWidth;
    private int charHeight;
    private int horizontalCount;
    private int numChars;

    public SpriteSheetFont(SpriteSheet font, char startingCharacter) {
        this.font = font;
        this.startingCharacter = startingCharacter;
        this.horizontalCount = font.getHorizontalCount();
        int verticalCount = font.getVerticalCount();
        this.charWidth = font.getWidth() / this.horizontalCount;
        this.charHeight = font.getHeight() / verticalCount;
        this.numChars = this.horizontalCount * verticalCount;
    }

    @Override
    public void drawString(float x, float y, String text) {
        this.drawString(x, y, text, Color.white);
    }

    @Override
    public void drawString(float x, float y, String text, Color col) {
        this.drawString(x, y, text, col, 0, text.length() - 1);
    }

    @Override
    public void drawString(float x, float y, String text, Color col, int startIndex, int endIndex) {
        try {
            byte[] data = text.getBytes("US-ASCII");
            for (int i = 0; i < data.length; ++i) {
                int index = data[i] - this.startingCharacter;
                if (index >= this.numChars) continue;
                int xPos = index % this.horizontalCount;
                int yPos = index / this.horizontalCount;
                if (i < startIndex || i > endIndex) continue;
                this.font.getSprite(xPos, yPos).draw(x + (float)(i * this.charWidth), y, col);
            }
        }
        catch (UnsupportedEncodingException e) {
            Log.error(e);
        }
    }

    @Override
    public int getHeight(String text) {
        return this.charHeight;
    }

    @Override
    public int getWidth(String text) {
        return this.charWidth * text.length();
    }

    @Override
    public int getLineHeight() {
        return this.charHeight;
    }
}
