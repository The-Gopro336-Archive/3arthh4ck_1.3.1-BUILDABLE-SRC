package org.newdawn.slick.openal;

import ibxm.Module;
import ibxm.OpenALMODPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.AudioImpl;
import org.newdawn.slick.openal.SoundStore;

public class MODSound
extends AudioImpl {
    private static OpenALMODPlayer player = new OpenALMODPlayer();
    private Module module;
    private SoundStore store;

    public MODSound(SoundStore store, InputStream in) throws IOException {
        this.store = store;
        this.module = OpenALMODPlayer.loadModule(in);
    }

    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        this.cleanUpSource();
        player.play(this.module, this.store.getSource(0), loop, SoundStore.get().isMusicOn());
        player.setup(pitch, 1.0f);
        this.store.setCurrentMusicVolume(gain);
        this.store.setMOD(this);
        return this.store.getSource(0);
    }

    private void cleanUpSource() {
        AL10.alSourceStop(this.store.getSource(0));
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        for (int queued = AL10.alGetSourcei(this.store.getSource(0), 4117); queued > 0; --queued) {
            AL10.alSourceUnqueueBuffers(this.store.getSource(0), buffer);
        }
        AL10.alSourcei(this.store.getSource(0), 4105, 0);
    }

    public void poll() {
        player.update();
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return -1;
    }

    @Override
    public void stop() {
        this.store.setMOD(null);
    }

    @Override
    public float getPosition() {
        throw new RuntimeException("Positioning on modules is not currently supported");
    }

    @Override
    public boolean setPosition(float position) {
        throw new RuntimeException("Positioning on modules is not currently supported");
    }
}
