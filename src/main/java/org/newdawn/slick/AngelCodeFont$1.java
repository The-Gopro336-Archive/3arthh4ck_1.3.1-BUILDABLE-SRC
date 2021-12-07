package org.newdawn.slick;

import java.util.LinkedHashMap;
import java.util.Map;
import org.newdawn.slick.AngelCodeFont;

class AngelCodeFont$1
extends LinkedHashMap {
    AngelCodeFont$1(int x0, float x1, boolean x2) {
        super(x0, x1, x2);
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        AngelCodeFont.this.eldestDisplayList = (AngelCodeFont.DisplayList)eldest.getValue();
        AngelCodeFont.this.eldestDisplayListID = ((AngelCodeFont)AngelCodeFont.this).eldestDisplayList.id;
        return false;
    }
}
