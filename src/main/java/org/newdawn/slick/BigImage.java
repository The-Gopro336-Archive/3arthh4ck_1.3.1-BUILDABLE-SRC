/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 */
package org.newdawn.slick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.OperationNotSupportedException;
import org.newdawn.slick.util.ResourceLoader;

public class BigImage
extends Image {
    protected static SGL GL = Renderer.get();
    private static Image lastBind;
    private Image[][] images;
    private int xcount;
    private int ycount;
    private int realWidth;
    private int realHeight;

    public static final int getMaxSingleImageSize() {
        IntBuffer buffer = BufferUtils.createIntBuffer((int)16);
        GL.glGetInteger(3379, buffer);
        return buffer.get(0);
    }

    private BigImage() {
        this.inited = true;
    }

    public BigImage(String ref) throws SlickException {
        this(ref, 2);
    }

    public BigImage(String ref, int filter) throws SlickException {
        this.build(ref, filter, BigImage.getMaxSingleImageSize());
    }

    public BigImage(String ref, int filter, int tileSize) throws SlickException {
        this.build(ref, filter, tileSize);
    }

    public BigImage(LoadableImageData data, ByteBuffer imageBuffer, int filter) {
        this.build(data, imageBuffer, filter, BigImage.getMaxSingleImageSize());
    }

    public BigImage(LoadableImageData data, ByteBuffer imageBuffer, int filter, int tileSize) {
        this.build(data, imageBuffer, filter, tileSize);
    }

    public Image getTile(int x, int y) {
        return this.images[x][y];
    }

    private void build(String ref, int filter, int tileSize) throws SlickException {
        try {
            LoadableImageData data = ImageDataFactory.getImageDataFor(ref);
            ByteBuffer imageBuffer = data.loadImage(ResourceLoader.getResourceAsStream(ref), false, null);
            this.build(data, imageBuffer, filter, tileSize);
        }
        catch (IOException e) {
            throw new SlickException("Failed to load: " + ref, e);
        }
    }

    private void build(final LoadableImageData data, final ByteBuffer imageBuffer, int filter, int tileSize) {
        final int dataWidth = data.getTexWidth();
        final int dataHeight = data.getTexHeight();
        this.realWidth = this.width = data.getWidth();
        this.realHeight = this.height = data.getHeight();
        if (dataWidth <= tileSize && dataHeight <= tileSize) {
            this.images = new Image[1][1];
            ImageData tempData = new ImageData(){

                @Override
                public int getDepth() {
                    return data.getDepth();
                }

                @Override
                public int getHeight() {
                    return dataHeight;
                }

                @Override
                public ByteBuffer getImageBufferData() {
                    return imageBuffer;
                }

                @Override
                public int getTexHeight() {
                    return dataHeight;
                }

                @Override
                public int getTexWidth() {
                    return dataWidth;
                }

                @Override
                public int getWidth() {
                    return dataWidth;
                }
            };
            this.images[0][0] = new Image(tempData, filter);
            this.xcount = 1;
            this.ycount = 1;
            this.inited = true;
            return;
        }
        this.xcount = (this.realWidth - 1) / tileSize + 1;
        this.ycount = (this.realHeight - 1) / tileSize + 1;
        this.images = new Image[this.xcount][this.ycount];
        int components = data.getDepth() / 8;
        for (int x = 0; x < this.xcount; ++x) {
            for (int y = 0; y < this.ycount; ++y) {
                int finalX = (x + 1) * tileSize;
                int finalY = (y + 1) * tileSize;
                final int imageWidth = Math.min(this.realWidth - x * tileSize, tileSize);
                final int imageHeight = Math.min(this.realHeight - y * tileSize, tileSize);
                final int xSize = tileSize;
                final int ySize = tileSize;
                final ByteBuffer subBuffer = BufferUtils.createByteBuffer((int)(tileSize * tileSize * components));
                int xo = x * tileSize * components;
                byte[] byteData = new byte[xSize * components];
                for (int i = 0; i < ySize; ++i) {
                    int yo = (y * tileSize + i) * dataWidth * components;
                    imageBuffer.position(yo + xo);
                    imageBuffer.get(byteData, 0, xSize * components);
                    subBuffer.put(byteData);
                }
                subBuffer.flip();
                ImageData imgData = new ImageData(){

                    @Override
                    public int getDepth() {
                        return data.getDepth();
                    }

                    @Override
                    public int getHeight() {
                        return imageHeight;
                    }

                    @Override
                    public int getWidth() {
                        return imageWidth;
                    }

                    @Override
                    public ByteBuffer getImageBufferData() {
                        return subBuffer;
                    }

                    @Override
                    public int getTexHeight() {
                        return ySize;
                    }

                    @Override
                    public int getTexWidth() {
                        return xSize;
                    }
                };
                this.images[x][y] = new Image(imgData, filter);
            }
        }
        this.inited = true;
    }

    @Override
    public void bind() {
        throw new OperationNotSupportedException("Can't bind big images yet");
    }

    @Override
    public Image copy() {
        throw new OperationNotSupportedException("Can't copy big images yet");
    }

    @Override
    public void draw() {
        this.draw(0.0f, 0.0f);
    }

    @Override
    public void draw(float x, float y, Color filter) {
        this.draw(x, y, this.width, this.height, filter);
    }

    @Override
    public void draw(float x, float y, float scale, Color filter) {
        this.draw(x, y, (float)this.width * scale, (float)this.height * scale, filter);
    }

    @Override
    public void draw(float x, float y, float width, float height, Color filter) {
        float sx = width / (float)this.realWidth;
        float sy = height / (float)this.realHeight;
        GL.glTranslatef(x, y, 0.0f);
        GL.glScalef(sx, sy, 1.0f);
        float xp = 0.0f;
        float yp = 0.0f;
        for (int tx = 0; tx < this.xcount; ++tx) {
            yp = 0.0f;
            for (int ty = 0; ty < this.ycount; ++ty) {
                Image image = this.images[tx][ty];
                image.draw(xp, yp, image.getWidth(), image.getHeight(), filter);
                yp += (float)image.getHeight();
                if (ty != this.ycount - 1) continue;
                xp += (float)image.getWidth();
            }
        }
        GL.glScalef(1.0f / sx, 1.0f / sy, 1.0f);
        GL.glTranslatef(-x, -y, 0.0f);
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
        int srcwidth = (int)(srcx2 - srcx);
        int srcheight = (int)(srcy2 - srcy);
        Image subImage = this.getSubImage((int)srcx, (int)srcy, srcwidth, srcheight);
        int width = (int)(x2 - x);
        int height = (int)(y2 - y);
        subImage.draw(x, y, (float)width, height);
    }

    @Override
    public void draw(float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
        int srcwidth = (int)(srcx2 - srcx);
        int srcheight = (int)(srcy2 - srcy);
        this.draw(x, y, srcwidth, srcheight, srcx, srcy, srcx2, srcy2);
    }

    @Override
    public void draw(float x, float y, float width, float height) {
        this.draw(x, y, width, height, Color.white);
    }

    @Override
    public void draw(float x, float y, float scale) {
        this.draw(x, y, scale, Color.white);
    }

    @Override
    public void draw(float x, float y) {
        this.draw(x, y, Color.white);
    }

    @Override
    public void drawEmbedded(float x, float y, float width, float height) {
        float sx = width / (float)this.realWidth;
        float sy = height / (float)this.realHeight;
        float xp = 0.0f;
        float yp = 0.0f;
        for (int tx = 0; tx < this.xcount; ++tx) {
            yp = 0.0f;
            for (int ty = 0; ty < this.ycount; ++ty) {
                Image image = this.images[tx][ty];
                if (lastBind == null || image.getTexture() != lastBind.getTexture()) {
                    if (lastBind != null) {
                        lastBind.endUse();
                    }
                    lastBind = image;
                    lastBind.startUse();
                }
                image.drawEmbedded(xp + x, yp + y, image.getWidth(), image.getHeight());
                yp += (float)image.getHeight();
                if (ty != this.ycount - 1) continue;
                xp += (float)image.getWidth();
            }
        }
    }

    @Override
    public void drawFlash(float x, float y, float width, float height) {
        float sx = width / (float)this.realWidth;
        float sy = height / (float)this.realHeight;
        GL.glTranslatef(x, y, 0.0f);
        GL.glScalef(sx, sy, 1.0f);
        float xp = 0.0f;
        float yp = 0.0f;
        for (int tx = 0; tx < this.xcount; ++tx) {
            yp = 0.0f;
            for (int ty = 0; ty < this.ycount; ++ty) {
                Image image = this.images[tx][ty];
                image.drawFlash(xp, yp, image.getWidth(), image.getHeight());
                yp += (float)image.getHeight();
                if (ty != this.ycount - 1) continue;
                xp += (float)image.getWidth();
            }
        }
        GL.glScalef(1.0f / sx, 1.0f / sy, 1.0f);
        GL.glTranslatef(-x, -y, 0.0f);
    }

    @Override
    public void drawFlash(float x, float y) {
        this.drawFlash(x, y, this.width, this.height);
    }

    @Override
    public void endUse() {
        if (lastBind != null) {
            lastBind.endUse();
        }
        lastBind = null;
    }

    @Override
    public void startUse() {
    }

    @Override
    public void ensureInverted() {
        throw new OperationNotSupportedException("Doesn't make sense for tiled operations");
    }

    @Override
    public Color getColor(int x, int y) {
        throw new OperationNotSupportedException("Can't use big images as buffers");
    }

    @Override
    public Image getFlippedCopy(boolean flipHorizontal, boolean flipVertical) {
        int y;
        int x;
        Image[][] images;
        BigImage image = new BigImage();
        image.images = this.images;
        image.xcount = this.xcount;
        image.ycount = this.ycount;
        image.width = this.width;
        image.height = this.height;
        image.realWidth = this.realWidth;
        image.realHeight = this.realHeight;
        if (flipHorizontal) {
            images = image.images;
            image.images = new Image[this.xcount][this.ycount];
            for (x = 0; x < this.xcount; ++x) {
                for (y = 0; y < this.ycount; ++y) {
                    image.images[x][y] = images[this.xcount - 1 - x][y].getFlippedCopy(true, false);
                }
            }
        }
        if (flipVertical) {
            images = image.images;
            image.images = new Image[this.xcount][this.ycount];
            for (x = 0; x < this.xcount; ++x) {
                for (y = 0; y < this.ycount; ++y) {
                    image.images[x][y] = images[x][this.ycount - 1 - y].getFlippedCopy(false, true);
                }
            }
        }
        return image;
    }

    @Override
    public Graphics getGraphics() throws SlickException {
        throw new OperationNotSupportedException("Can't use big images as offscreen buffers");
    }

    @Override
    public Image getScaledCopy(float scale) {
        return this.getScaledCopy((int)(scale * (float)this.width), (int)(scale * (float)this.height));
    }

    @Override
    public Image getScaledCopy(int width, int height) {
        BigImage image = new BigImage();
        image.images = this.images;
        image.xcount = this.xcount;
        image.ycount = this.ycount;
        image.width = width;
        image.height = height;
        image.realWidth = this.realWidth;
        image.realHeight = this.realHeight;
        return image;
    }

    @Override
    public Image getSubImage(int x, int y, int width, int height) {
        BigImage image = new BigImage();
        image.width = width;
        image.height = height;
        image.realWidth = width;
        image.realHeight = height;
        image.images = new Image[this.xcount][this.ycount];
        float xp = 0.0f;
        float yp = 0.0f;
        int x2 = x + width;
        int y2 = y + height;
        int startx = 0;
        int starty = 0;
        boolean foundStart = false;
        for (int xt = 0; xt < this.xcount; ++xt) {
            yp = 0.0f;
            starty = 0;
            foundStart = false;
            for (int yt = 0; yt < this.ycount; ++yt) {
                Image current = this.images[xt][yt];
                int xp2 = (int)(xp + (float)current.getWidth());
                int yp2 = (int)(yp + (float)current.getHeight());
                int targetX1 = (int)Math.max((float)x, xp);
                int targetY1 = (int)Math.max((float)y, yp);
                int targetX2 = Math.min(x2, xp2);
                int targetY2 = Math.min(y2, yp2);
                int targetWidth = targetX2 - targetX1;
                int targetHeight = targetY2 - targetY1;
                if (targetWidth > 0 && targetHeight > 0) {
                    Image subImage = current.getSubImage((int)((float)targetX1 - xp), (int)((float)targetY1 - yp), targetX2 - targetX1, targetY2 - targetY1);
                    foundStart = true;
                    image.images[startx][starty] = subImage;
                    image.ycount = Math.max(image.ycount, ++starty);
                }
                yp += (float)current.getHeight();
                if (yt != this.ycount - 1) continue;
                xp += (float)current.getWidth();
            }
            if (!foundStart) continue;
            ++startx;
            ++image.xcount;
        }
        return image;
    }

    @Override
    public Texture getTexture() {
        throw new OperationNotSupportedException("Can't use big images as offscreen buffers");
    }

    @Override
    protected void initImpl() {
        throw new OperationNotSupportedException("Can't use big images as offscreen buffers");
    }

    @Override
    protected void reinit() {
        throw new OperationNotSupportedException("Can't use big images as offscreen buffers");
    }

    @Override
    public void setTexture(Texture texture) {
        throw new OperationNotSupportedException("Can't use big images as offscreen buffers");
    }

    public Image getSubImage(int offsetX, int offsetY) {
        return this.images[offsetX][offsetY];
    }

    public int getHorizontalImageCount() {
        return this.xcount;
    }

    public int getVerticalImageCount() {
        return this.ycount;
    }

    @Override
    public String toString() {
        return "[BIG IMAGE]";
    }

    @Override
    public void destroy() throws SlickException {
        for (int tx = 0; tx < this.xcount; ++tx) {
            for (int ty = 0; ty < this.ycount; ++ty) {
                Image image = this.images[tx][ty];
                image.destroy();
            }
        }
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
        int srcwidth = (int)(srcx2 - srcx);
        int srcheight = (int)(srcy2 - srcy);
        Image subImage = this.getSubImage((int)srcx, (int)srcy, srcwidth, srcheight);
        int width = (int)(x2 - x);
        int height = (int)(y2 - y);
        subImage.draw(x, y, width, height, filter);
    }

    @Override
    public void drawCentered(float x, float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawFlash(float x, float y, float width, float height, Color col) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawSheared(float x, float y, float hshear, float vshear) {
        throw new UnsupportedOperationException();
    }
}

