package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;

class TextureImpl$ReloadData {
    private int srcPixelFormat;
    private int componentCount;
    private int minFilter;
    private int magFilter;
    private ByteBuffer textureBuffer;

    private TextureImpl$ReloadData() {
    }

    public int reload() {
        Log.error("Reloading texture: " + TextureImpl.this.ref);
        return InternalTextureLoader.get().reload(TextureImpl.this, this.srcPixelFormat, this.componentCount, this.minFilter, this.magFilter, this.textureBuffer);
    }

    static int access$102(TextureImpl$ReloadData x0, int x1) {
        x0.srcPixelFormat = x1;
        return x0.srcPixelFormat;
    }

    static int access$202(TextureImpl$ReloadData x0, int x1) {
        x0.componentCount = x1;
        return x0.componentCount;
    }

    static int access$302(TextureImpl$ReloadData x0, int x1) {
        x0.minFilter = x1;
        return x0.minFilter;
    }

    static int access$402(TextureImpl$ReloadData x0, int x1) {
        x0.magFilter = x1;
        return x0.magFilter;
    }

    static ByteBuffer access$502(TextureImpl$ReloadData x0, ByteBuffer x1) {
        x0.textureBuffer = x1;
        return x0.textureBuffer;
    }
}
