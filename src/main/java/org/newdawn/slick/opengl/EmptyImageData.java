package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;

public class EmptyImageData
implements ImageData {
    private int width;
    private int height;

    public EmptyImageData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getDepth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public ByteBuffer getImageBufferData() {
        return BufferUtils.createByteBuffer(this.getTexWidth() * this.getTexHeight() * 4);
    }

    @Override
    public int getTexHeight() {
        return InternalTextureLoader.get2Fold(this.height);
    }

    @Override
    public int getTexWidth() {
        return InternalTextureLoader.get2Fold(this.width);
    }

    @Override
    public int getWidth() {
        return this.width;
    }
}
