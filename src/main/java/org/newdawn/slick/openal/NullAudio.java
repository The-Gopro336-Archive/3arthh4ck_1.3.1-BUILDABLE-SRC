/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.openal;

import org.newdawn.slick.openal.Audio;

public class NullAudio
implements Audio {
    @Override
    public int getBufferID() {
        return 0;
    }

    @Override
    public float getPosition() {
        return 0.0f;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        return 0;
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return 0;
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
        return 0;
    }

    @Override
    public boolean setPosition(float position) {
        return false;
    }

    @Override
    public void stop() {
    }
}

