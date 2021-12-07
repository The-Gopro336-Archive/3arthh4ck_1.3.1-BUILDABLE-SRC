/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class FontUtils {
    public static void drawLeft(Font font, String s, int x, int y) {
        FontUtils.drawString(font, s, 1, x, y, 0, Color.white);
    }

    public static void drawCenter(Font font, String s, int x, int y, int width) {
        FontUtils.drawString(font, s, 2, x, y, width, Color.white);
    }

    public static void drawCenter(Font font, String s, int x, int y, int width, Color color) {
        FontUtils.drawString(font, s, 2, x, y, width, color);
    }

    public static void drawRight(Font font, String s, int x, int y, int width) {
        FontUtils.drawString(font, s, 3, x, y, width, Color.white);
    }

    public static void drawRight(Font font, String s, int x, int y, int width, Color color) {
        FontUtils.drawString(font, s, 3, x, y, width, color);
    }

    public static final int drawString(Font font, String s, int alignment, int x, int y, int width, Color color) {
        int resultingXCoordinate = 0;
        if (alignment == 1) {
            font.drawString(x, y, s, color);
        } else if (alignment == 2) {
            font.drawString(x + width / 2 - font.getWidth(s) / 2, y, s, color);
        } else if (alignment == 3) {
            font.drawString(x + width - font.getWidth(s), y, s, color);
        } else if (alignment == 4) {
            int leftWidth = width - font.getWidth(s);
            if (leftWidth <= 0) {
                font.drawString(x, y, s, color);
            }
            return FontUtils.drawJustifiedSpaceSeparatedSubstrings(font, s, x, y, FontUtils.calculateWidthOfJustifiedSpaceInPixels(font, s, leftWidth));
        }
        return resultingXCoordinate;
    }

    private static int calculateWidthOfJustifiedSpaceInPixels(Font font, String s, int leftWidth) {
        int space = 0;
        int curpos = 0;
        while (curpos < s.length()) {
            if (s.charAt(curpos++) != ' ') continue;
            ++space;
        }
        if (space > 0) {
            space = (leftWidth + font.getWidth(" ") * space) / space;
        }
        return space;
    }

    private static int drawJustifiedSpaceSeparatedSubstrings(Font font, String s, int x, int y, int justifiedSpaceWidth) {
        int curpos = 0;
        int endpos = 0;
        int resultingXCoordinate = x;
        while (curpos < s.length()) {
            endpos = s.indexOf(32, curpos);
            if (endpos == -1) {
                endpos = s.length();
            }
            String substring = s.substring(curpos, endpos);
            font.drawString(resultingXCoordinate, y, substring);
            resultingXCoordinate += font.getWidth(substring) + justifiedSpaceWidth;
            curpos = endpos + 1;
        }
        return resultingXCoordinate;
    }

    public class Alignment {
        public static final int LEFT = 1;
        public static final int CENTER = 2;
        public static final int RIGHT = 3;
        public static final int JUSTIFY = 4;
    }
}

