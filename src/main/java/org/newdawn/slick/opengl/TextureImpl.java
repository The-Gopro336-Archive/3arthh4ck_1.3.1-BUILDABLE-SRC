package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

public class TextureImpl
implements Texture {
    protected static SGL GL = Renderer.get();
    static Texture lastBind;
    private int target;
    private int textureID;
    private int height;
    private int width;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;
    private boolean alpha;
    private String ref;
    private String cacheName;
    private ReloadData reloadData;

    public static Texture getLastBind() {
        return lastBind;
    }

    protected TextureImpl() {
    }

    public TextureImpl(String ref, int target, int textureID) {
        this.target = target;
        this.ref = ref;
        this.textureID = textureID;
        lastBind = this;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public boolean hasAlpha() {
        return this.alpha;
    }

    @Override
    public String getTextureRef() {
        return this.ref;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public static void bindNone() {
        lastBind = null;
        GL.glDisable(3553);
    }

    public static void unbind() {
        lastBind = null;
    }

    @Override
    public void bind() {
        if (lastBind != this) {
            lastBind = this;
            GL.glEnable(3553);
            GL.glBindTexture(this.target, this.textureID);
        }
    }

    public void setHeight(int height) {
        this.height = height;
        this.setHeight();
    }

    public void setWidth(int width) {
        this.width = width;
        this.setWidth();
    }

    @Override
    public int getImageHeight() {
        return this.height;
    }

    @Override
    public int getImageWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.heightRatio;
    }

    @Override
    public float getWidth() {
        return this.widthRatio;
    }

    @Override
    public int getTextureHeight() {
        return this.texHeight;
    }

    @Override
    public int getTextureWidth() {
        return this.texWidth;
    }

    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        this.setHeight();
    }

    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        this.setWidth();
    }

    private void setHeight() {
        if (this.texHeight != 0) {
            this.heightRatio = (float)this.height / (float)this.texHeight;
        }
    }

    private void setWidth() {
        if (this.texWidth != 0) {
            this.widthRatio = (float)this.width / (float)this.texWidth;
        }
    }

    @Override
    public void release() {
        IntBuffer texBuf = this.createIntBuffer(1);
        texBuf.put(this.textureID);
        texBuf.flip();
        GL.glDeleteTextures(texBuf);
        if (lastBind == this) {
            TextureImpl.bindNone();
        }
        if (this.cacheName != null) {
            InternalTextureLoader.get().clear(this.cacheName);
        } else {
            InternalTextureLoader.get().clear(this.ref);
        }
    }

    @Override
    public int getTextureID() {
        return this.textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    protected IntBuffer createIntBuffer(int size) {
        ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());
        return temp.asIntBuffer();
    }

    @Override
    public byte[] getTextureData() {
        ByteBuffer buffer = BufferUtils.createByteBuffer((this.hasAlpha() ? 4 : 3) * this.texWidth * this.texHeight);
        this.bind();
        GL.glGetTexImage(3553, 0, this.hasAlpha() ? 6408 : 6407, 5121, buffer);
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();
        return data;
    }

    @Override
    public void setTextureFilter(int textureFilter) {
        this.bind();
        GL.glTexParameteri(this.target, 10241, textureFilter);
        GL.glTexParameteri(this.target, 10240, textureFilter);
    }

    public void setTextureData(int srcPixelFormat, int componentCount, int minFilter, int magFilter, ByteBuffer textureBuffer) {
        this.reloadData = new ReloadData();
        this.reloadData.srcPixelFormat = srcPixelFormat;
        this.reloadData.componentCount = componentCount;
        this.reloadData.minFilter = minFilter;
        this.reloadData.magFilter = magFilter;
        this.reloadData.textureBuffer = textureBuffer;
    }

    public void reload() {
        if (this.reloadData != null) {
            this.textureID = this.reloadData.reload();
        }
    }

    private class ReloadData {
        private int srcPixelFormat;
        private int componentCount;
        private int minFilter;
        private int magFilter;
        private ByteBuffer textureBuffer;

        private ReloadData() {
        }

        public int reload() {
            Log.error("Reloading texture: " + TextureImpl.this.ref);
            return InternalTextureLoader.get().reload(TextureImpl.this, this.srcPixelFormat, this.componentCount, this.minFilter, this.magFilter, this.textureBuffer);
        }
    }
}
