package org.newdawn.slick;

import java.nio.ByteBuffer;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.LoadableImageData;

class BigImage$2
implements ImageData {
    final LoadableImageData val$data;
    final int val$imageHeight;
    final int val$imageWidth;
    final ByteBuffer val$subBuffer;
    final int val$ySize;
    final int val$xSize;

    BigImage$2(LoadableImageData loadableImageData, int n, int n2, ByteBuffer byteBuffer, int n3, int n4) {
        this.val$data = loadableImageData;
        this.val$imageHeight = n;
        this.val$imageWidth = n2;
        this.val$subBuffer = byteBuffer;
        this.val$ySize = n3;
        this.val$xSize = n4;
    }

    @Override
    public int getDepth() {
        return this.val$data.getDepth();
    }

    @Override
    public int getHeight() {
        return this.val$imageHeight;
    }

    @Override
    public int getWidth() {
        return this.val$imageWidth;
    }

    @Override
    public ByteBuffer getImageBufferData() {
        return this.val$subBuffer;
    }

    @Override
    public int getTexHeight() {
        return this.val$ySize;
    }

    @Override
    public int getTexWidth() {
        return this.val$xSize;
    }
}
