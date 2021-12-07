/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.render.image;

import java.awt.image.BufferedImage;

public class ImageFrame {
    private final int delay;
    private final BufferedImage image;
    private final String disposal;
    private final int width;
    private final int height;

    public ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
        this.image = image;
        this.delay = delay;
        this.disposal = disposal;
        this.width = width;
        this.height = height;
    }

    public ImageFrame(BufferedImage image) {
        this.image = image;
        this.delay = -1;
        this.disposal = null;
        this.width = -1;
        this.height = -1;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getDelay() {
        return this.delay;
    }

    public String getDisposal() {
        return this.disposal;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

