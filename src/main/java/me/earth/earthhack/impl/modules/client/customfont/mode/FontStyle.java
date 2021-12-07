package me.earth.earthhack.impl.modules.client.customfont.mode;

public enum FontStyle {
    Plain(0),
    Bold(1),
    Italic(2),
    All(3);

    int style;

    private FontStyle(int style) {
        this.style = style;
    }

    public int getFontStyle() {
        return this.style;
    }
}
