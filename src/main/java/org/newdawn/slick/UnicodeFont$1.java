package org.newdawn.slick;

import java.util.Comparator;
import org.newdawn.slick.font.Glyph;

final class UnicodeFont$1
implements Comparator {
    UnicodeFont$1() {
    }

    public int compare(Object o1, Object o2) {
        return ((Glyph)o1).getHeight() - ((Glyph)o2).getHeight();
    }
}
