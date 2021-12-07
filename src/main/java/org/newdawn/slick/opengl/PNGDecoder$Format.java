package org.newdawn.slick.opengl;

public class PNGDecoder$Format {
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
