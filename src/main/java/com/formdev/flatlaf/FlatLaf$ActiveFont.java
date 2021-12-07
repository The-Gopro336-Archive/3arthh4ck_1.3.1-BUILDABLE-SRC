package com.formdev.flatlaf;

import java.awt.Font;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;

class FlatLaf$ActiveFont
implements UIDefaults.ActiveValue {
    private final float scaleFactor;
    private Font font;
    private Font lastDefaultFont;

    FlatLaf$ActiveFont(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public Object createValue(UIDefaults table) {
        Font defaultFont = UIManager.getFont("defaultFont");
        if (defaultFont == null) {
            defaultFont = UIManager.getFont("Label.font");
        }
        if (this.lastDefaultFont != defaultFont) {
            this.lastDefaultFont = defaultFont;
            if (this.scaleFactor != 1.0f) {
                int newFontSize = Math.round((float)defaultFont.getSize() * this.scaleFactor);
                this.font = new FontUIResource(defaultFont.deriveFont((float)newFontSize));
            } else {
                this.font = defaultFont instanceof UIResource ? defaultFont : new FontUIResource(defaultFont);
            }
        }
        return this.font;
    }
}
