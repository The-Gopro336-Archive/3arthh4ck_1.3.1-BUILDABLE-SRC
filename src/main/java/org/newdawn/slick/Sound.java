/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import java.io.InputStream;
import java.net.URL;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;

public class Sound {
    private Audio sound;

    public Sound(InputStream in, String ref) throws SlickException {
        block6: {
            SoundStore.get().init();
            try {
                if (ref.toLowerCase().endsWith(".ogg")) {
                    this.sound = SoundStore.get().getOgg(in);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".wav")) {
                    this.sound = SoundStore.get().getWAV(in);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif")) {
                    this.sound = SoundStore.get().getAIF(in);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(in);
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .aif, .wav and .ogg are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load sound: " + ref);
            }
        }
    }

    public Sound(URL url) throws SlickException {
        block6: {
            SoundStore.get().init();
            String ref = url.getFile();
            try {
                if (ref.toLowerCase().endsWith(".ogg")) {
                    this.sound = SoundStore.get().getOgg(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".wav")) {
                    this.sound = SoundStore.get().getWAV(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif")) {
                    this.sound = SoundStore.get().getAIF(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(url.openStream());
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .aif, .wav and .ogg are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load sound: " + ref);
            }
        }
    }

    public Sound(String ref) throws SlickException {
        block6: {
            SoundStore.get().init();
            try {
                if (ref.toLowerCase().endsWith(".ogg")) {
                    this.sound = SoundStore.get().getOgg(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".wav")) {
                    this.sound = SoundStore.get().getWAV(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif")) {
                    this.sound = SoundStore.get().getAIF(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(ref);
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .aif, .wav and .ogg are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load sound: " + ref);
            }
        }
    }

    public void play() {
        this.play(1.0f, 1.0f);
    }

    public void play(float pitch, float volume) {
        this.sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), false);
    }

    public void playAt(float x, float y, float z) {
        this.playAt(1.0f, 1.0f, x, y, z);
    }

    public void playAt(float pitch, float volume, float x, float y, float z) {
        this.sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), false, x, y, z);
    }

    public void loop() {
        this.loop(1.0f, 1.0f);
    }

    public void loop(float pitch, float volume) {
        this.sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), true);
    }

    public boolean playing() {
        return this.sound.isPlaying();
    }

    public void stop() {
        this.sound.stop();
    }
}

