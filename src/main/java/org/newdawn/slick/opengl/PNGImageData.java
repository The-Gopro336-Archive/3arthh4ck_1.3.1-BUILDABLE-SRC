package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.PNGDecoder;

public class PNGImageData
implements LoadableImageData {
    private int width;
    private int height;
    private int texHeight;
    private int texWidth;
    private PNGDecoder decoder;
    private int bitDepth;
    private ByteBuffer scratch;

    @Override
    public int getDepth() {
        return this.bitDepth;
    }

    @Override
    public ByteBuffer getImageBufferData() {
        return this.scratch;
    }

    @Override
    public int getTexHeight() {
        return this.texHeight;
    }

    @Override
    public int getTexWidth() {
        return this.texWidth;
    }

    @Override
    public ByteBuffer loadImage(InputStream fis) throws IOException {
        return this.loadImage(fis, false, null);
    }

    @Override
    public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
        return this.loadImage(fis, flipped, false, transparent);
    }

    @Override
    public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
        if (transparent != null) {
            forceAlpha = true;
            throw new IOException("Transparent color not support in custom PNG Decoder");
        }
        PNGDecoder decoder = new PNGDecoder(fis);
        if (!decoder.isRGB()) {
            throw new IOException("Only RGB formatted images are supported by the PNGLoader");
        }
        this.width = decoder.getWidth();
        this.height = decoder.getHeight();
        this.texWidth = this.get2Fold(this.width);
        this.texHeight = this.get2Fold(this.height);
        int perPixel = decoder.hasAlpha() ? 4 : 3;
        this.bitDepth = decoder.hasAlpha() ? 32 : 24;
        this.scratch = BufferUtils.createByteBuffer(this.texWidth * this.texHeight * perPixel);
        decoder.decode(this.scratch, this.texWidth * perPixel, perPixel == 4 ? PNGDecoder.RGBA : PNGDecoder.RGB);
        if (this.height < this.texHeight - 1) {
            int topOffset = (this.texHeight - 1) * (this.texWidth * perPixel);
            int bottomOffset = (this.height - 1) * (this.texWidth * perPixel);
            for (int x = 0; x < this.texWidth; ++x) {
                for (int i = 0; i < perPixel; ++i) {
                    this.scratch.put(topOffset + x + i, this.scratch.get(x + i));
                    this.scratch.put(bottomOffset + this.texWidth * perPixel + x + i, this.scratch.get(bottomOffset + x + i));
                }
            }
        }
        if (this.width < this.texWidth - 1) {
            for (int y = 0; y < this.texHeight; ++y) {
                for (int i = 0; i < perPixel; ++i) {
                    this.scratch.put((y + 1) * (this.texWidth * perPixel) - perPixel + i, this.scratch.get(y * (this.texWidth * perPixel) + i));
                    this.scratch.put(y * (this.texWidth * perPixel) + this.width * perPixel + i, this.scratch.get(y * (this.texWidth * perPixel) + (this.width - 1) * perPixel + i));
                }
            }
        }
        if (!decoder.hasAlpha() && forceAlpha) {
            ByteBuffer temp = BufferUtils.createByteBuffer(this.texWidth * this.texHeight * 4);
            for (int x = 0; x < this.texWidth; ++x) {
                for (int y = 0; y < this.texHeight; ++y) {
                    int srcOffset = y * 3 + x * this.texHeight * 3;
                    int dstOffset = y * 4 + x * this.texHeight * 4;
                    temp.put(dstOffset, this.scratch.get(srcOffset));
                    temp.put(dstOffset + 1, this.scratch.get(srcOffset + 1));
                    temp.put(dstOffset + 2, this.scratch.get(srcOffset + 2));
                    if (x < this.getHeight() && y < this.getWidth()) {
                        temp.put(dstOffset + 3, (byte)-1);
                        continue;
                    }
                    temp.put(dstOffset + 3, (byte)0);
                }
            }
            this.bitDepth = 32;
            this.scratch = temp;
        }
        if (transparent != null) {
            for (int i = 0; i < this.texWidth * this.texHeight * 4; i += 4) {
                boolean match = true;
                for (int c = 0; c < 3; ++c) {
                    if (this.toInt(this.scratch.get(i + c)) == transparent[c]) continue;
                    match = false;
                }
                if (!match) continue;
                this.scratch.put(i + 3, (byte)0);
            }
        }
        this.scratch.position(0);
        return this.scratch;
    }

    private int toInt(byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }

    private int get2Fold(int fold) {
        int ret;
        for (ret = 2; ret < fold; ret *= 2) {
        }
        return ret;
    }

    @Override
    public void configureEdging(boolean edging) {
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
