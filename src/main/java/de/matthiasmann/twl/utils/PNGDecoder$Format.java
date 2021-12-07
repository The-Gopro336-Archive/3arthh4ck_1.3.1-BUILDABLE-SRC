package de.matthiasmann.twl.utils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PNGDecoder$Format {
    ALPHA(1, true),
    LUMINANCE(1, false),
    LUMINANCE_ALPHA(2, true),
    RGB(3, false),
    RGBA(4, true),
    BGRA(4, true),
    ABGR(4, true);

    final int numComponents;
    final boolean hasAlpha;

    private PNGDecoder$Format(int numComponents, boolean hasAlpha) {
        this.numComponents = numComponents;
        this.hasAlpha = hasAlpha;
    }

    public int getNumComponents() {
        return this.numComponents;
    }

    public boolean isHasAlpha() {
        return this.hasAlpha;
    }
}
