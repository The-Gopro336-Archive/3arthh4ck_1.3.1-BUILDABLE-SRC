package org.newdawn.slick;

import java.nio.ByteBuffer;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.LoadableImageData;

class BigImage$1
implements ImageData {
    final LoadableImageData val$data;
    final int val$dataHeight;
    final ByteBuffer val$imageBuffer;
    final int val$dataWidth;

    BigImage$1(LoadableImageData loadableImageData, int n, ByteBuffer byteBuffer, int n2) {
        this.val$data = loadableImageData;
        this.val$dataHeight = n;
        this.val$imageBuffer = byteBuffer;
        this.val$dataWidth = n2;
    }

    @Override
    public int getDepth() {
        return this.val$data.getDepth();
    }

    @Override
    public int getHeight() {
        return this.val$dataHeight;
    }

    @Override
    public ByteBuffer getImageBufferData() {
        return this.val$imageBuffer;
    }

    @Override
    public int getTexHeight() {
        return this.val$dataHeight;
    }

    @Override
    public int getTexWidth() {
        return this.val$dataWidth;
    }

    @Override
    public int getWidth() {
        return this.val$dataWidth;
    }
}
