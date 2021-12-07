package org.newdawn.slick.openal;

import java.io.IOException;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.AudioImpl;
import org.newdawn.slick.openal.OpenALStreamPlayer;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;

public class StreamSound
extends AudioImpl {
    private OpenALStreamPlayer player;

    public StreamSound(OpenALStreamPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isPlaying() {
        return SoundStore.get().isPlaying(this.player);
    }

    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        try {
            this.cleanUpSource();
            this.player.setup(pitch);
            this.player.play(loop);
            SoundStore.get().setStream(this.player);
        }
        catch (IOException e) {
            Log.error("Failed to read OGG source: " + this.player.getSource());
        }
        return SoundStore.get().getSource(0);
    }

    private void cleanUpSource() {
        SoundStore store = SoundStore.get();
        AL10.alSourceStop(store.getSource(0));
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        for (int queued = AL10.alGetSourcei(store.getSource(0), 4117); queued > 0; --queued) {
            AL10.alSourceUnqueueBuffers(store.getSource(0), buffer);
        }
        AL10.alSourcei(store.getSource(0), 4105, 0);
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
        return this.playAsMusic(pitch, gain, loop);
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return this.playAsMusic(pitch, gain, loop);
    }

    @Override
    public void stop() {
        SoundStore.get().setStream(null);
    }

    @Override
    public boolean setPosition(float position) {
        return this.player.setPosition(position);
    }

    @Override
    public float getPosition() {
        return this.player.getPosition();
    }
}
