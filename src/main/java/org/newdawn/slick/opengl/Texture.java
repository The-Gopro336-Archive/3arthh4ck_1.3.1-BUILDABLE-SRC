/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl;

public interface Texture {
    public boolean hasAlpha();

    public String getTextureRef();

    public void bind();

    public int getImageHeight();

    public int getImageWidth();

    public float getHeight();

    public float getWidth();

    public int getTextureHeight();

    public int getTextureWidth();

    public void release();

    public int getTextureID();

    public byte[] getTextureData();

    public void setTextureFilter(int var1);
}

