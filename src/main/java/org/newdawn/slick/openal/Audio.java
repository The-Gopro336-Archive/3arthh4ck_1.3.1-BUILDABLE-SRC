/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.openal;

public interface Audio {
    public void stop();

    public int getBufferID();

    public boolean isPlaying();

    public int playAsSoundEffect(float var1, float var2, boolean var3);

    public int playAsSoundEffect(float var1, float var2, boolean var3, float var4, float var5, float var6);

    public int playAsMusic(float var1, float var2, boolean var3);

    public boolean setPosition(float var1);

    public float getPosition();
}

