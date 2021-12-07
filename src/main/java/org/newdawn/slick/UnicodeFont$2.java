package org.newdawn.slick;

import java.util.LinkedHashMap;
import java.util.Map;
import org.newdawn.slick.UnicodeFont;

class UnicodeFont$2
extends LinkedHashMap {
    UnicodeFont$2(int x0, float x1, boolean x2) {
        super(x0, x1, x2);
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        UnicodeFont.DisplayList displayList = (UnicodeFont.DisplayList)eldest.getValue();
        if (displayList != null) {
            UnicodeFont.this.eldestDisplayListID = displayList.id;
        }
        return this.size() > 200;
    }
}
