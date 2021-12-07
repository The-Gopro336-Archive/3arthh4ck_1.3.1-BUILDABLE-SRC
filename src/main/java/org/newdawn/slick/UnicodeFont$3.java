package org.newdawn.slick;

import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;

class UnicodeFont$3
extends Glyph {
    UnicodeFont$3(int x0, Rectangle x1, GlyphVector x2, int x3, UnicodeFont x4) {
        super(x0, x1, x2, x3, x4);
    }

    @Override
    public boolean isMissing() {
        return true;
    }
}
