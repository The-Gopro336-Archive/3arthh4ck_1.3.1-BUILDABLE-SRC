package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.TextureImpl;

public class DeferredTexture
extends TextureImpl
implements DeferredResource {
    private InputStream in;
    private String resourceName;
    private boolean flipped;
    private int filter;
    private TextureImpl target;
    private int[] trans;

    public DeferredTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] trans) {
        this.in = in;
        this.resourceName = resourceName;
        this.flipped = flipped;
        this.filter = filter;
        this.trans = trans;
        LoadingList.get().add(this);
    }

    @Override
    public void load() throws IOException {
        boolean before = InternalTextureLoader.get().isDeferredLoading();
        InternalTextureLoader.get().setDeferredLoading(false);
        this.target = InternalTextureLoader.get().getTexture(this.in, this.resourceName, this.flipped, this.filter, this.trans);
        InternalTextureLoader.get().setDeferredLoading(before);
    }

    private void checkTarget() {
        if (this.target == null) {
            try {
                this.load();
                LoadingList.get().remove(this);
                return;
            }
            catch (IOException e) {
                throw new RuntimeException("Attempt to use deferred texture before loading and resource not found: " + this.resourceName);
            }
        }
    }

    @Override
    public void bind() {
        this.checkTarget();
        this.target.bind();
    }

    @Override
    public float getHeight() {
        this.checkTarget();
        return this.target.getHeight();
    }

    @Override
    public int getImageHeight() {
        this.checkTarget();
        return this.target.getImageHeight();
    }

    @Override
    public int getImageWidth() {
        this.checkTarget();
        return this.target.getImageWidth();
    }

    @Override
    public int getTextureHeight() {
        this.checkTarget();
        return this.target.getTextureHeight();
    }

    @Override
    public int getTextureID() {
        this.checkTarget();
        return this.target.getTextureID();
    }

    @Override
    public String getTextureRef() {
        this.checkTarget();
        return this.target.getTextureRef();
    }

    @Override
    public int getTextureWidth() {
        this.checkTarget();
        return this.target.getTextureWidth();
    }

    @Override
    public float getWidth() {
        this.checkTarget();
        return this.target.getWidth();
    }

    @Override
    public void release() {
        this.checkTarget();
        this.target.release();
    }

    @Override
    public void setAlpha(boolean alpha) {
        this.checkTarget();
        this.target.setAlpha(alpha);
    }

    @Override
    public void setHeight(int height) {
        this.checkTarget();
        this.target.setHeight(height);
    }

    @Override
    public void setTextureHeight(int texHeight) {
        this.checkTarget();
        this.target.setTextureHeight(texHeight);
    }

    @Override
    public void setTextureID(int textureID) {
        this.checkTarget();
        this.target.setTextureID(textureID);
    }

    @Override
    public void setTextureWidth(int texWidth) {
        this.checkTarget();
        this.target.setTextureWidth(texWidth);
    }

    @Override
    public void setWidth(int width) {
        this.checkTarget();
        this.target.setWidth(width);
    }

    @Override
    public byte[] getTextureData() {
        this.checkTarget();
        return this.target.getTextureData();
    }

    @Override
    public String getDescription() {
        return this.resourceName;
    }

    @Override
    public boolean hasAlpha() {
        this.checkTarget();
        return this.target.hasAlpha();
    }

    @Override
    public void setTextureFilter(int textureFilter) {
        this.checkTarget();
        this.target.setTextureFilter(textureFilter);
    }
}
