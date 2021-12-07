/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioImpl;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;

public class Music {
    private static Music currentMusic;
    private Audio sound;
    private boolean playing;
    private ArrayList<MusicListener> listeners;
    private float volume;
    private float fadeStartGain;
    private float fadeEndGain;
    private int fadeTime;
    private int fadeDuration;
    private boolean stopAfterFade;
    private boolean positioning;
    private float requiredPosition;

    public static void poll(int delta) {
        if (currentMusic != null) {
            SoundStore.get().poll(delta);
            if (!SoundStore.get().isMusicPlaying()) {
                if (!Music.currentMusic.positioning) {
                    Music oldMusic = currentMusic;
                    currentMusic = null;
                    oldMusic.fireMusicEnded();
                }
            } else {
                currentMusic.update(delta);
            }
        }
    }

    public Music(String ref) throws SlickException {
        this(ref, false);
    }

    public Music(URL ref) throws SlickException {
        this(ref, false);
    }

    public Music(InputStream in, String ref) throws SlickException {
        block6: {
            this.listeners = new ArrayList();
            this.volume = 1.0f;
            this.requiredPosition = -1.0f;
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
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(in);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif") || ref.toLowerCase().endsWith(".aiff")) {
                    this.sound = SoundStore.get().getAIF(in);
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .ogg, and .aif/f are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load music: " + ref);
            }
        }
    }

    public Music(URL url, boolean streamingHint) throws SlickException {
        block6: {
            this.listeners = new ArrayList();
            this.volume = 1.0f;
            this.requiredPosition = -1.0f;
            SoundStore.get().init();
            String ref = url.getFile();
            try {
                if (ref.toLowerCase().endsWith(".ogg")) {
                    this.sound = streamingHint ? SoundStore.get().getOggStream(url) : SoundStore.get().getOgg(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".wav")) {
                    this.sound = SoundStore.get().getWAV(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(url.openStream());
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif") || ref.toLowerCase().endsWith(".aiff")) {
                    this.sound = SoundStore.get().getAIF(url.openStream());
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .ogg, and .aif/f are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load sound: " + url);
            }
        }
    }

    public Music(String ref, boolean streamingHint) throws SlickException {
        block6: {
            this.listeners = new ArrayList();
            this.volume = 1.0f;
            this.requiredPosition = -1.0f;
            SoundStore.get().init();
            try {
                if (ref.toLowerCase().endsWith(".ogg")) {
                    this.sound = streamingHint ? SoundStore.get().getOggStream(ref) : SoundStore.get().getOgg(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".wav")) {
                    this.sound = SoundStore.get().getWAV(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
                    this.sound = SoundStore.get().getMOD(ref);
                    break block6;
                }
                if (ref.toLowerCase().endsWith(".aif") || ref.toLowerCase().endsWith(".aiff")) {
                    this.sound = SoundStore.get().getAIF(ref);
                    break block6;
                }
                throw new SlickException("Only .xm, .mod, .ogg, and .aif/f are currently supported.");
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to load sound: " + ref);
            }
        }
    }

    public void addListener(MusicListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(MusicListener listener) {
        this.listeners.remove(listener);
    }

    private void fireMusicEnded() {
        this.playing = false;
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).musicEnded(this);
        }
    }

    private void fireMusicSwapped(Music newMusic) {
        this.playing = false;
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).musicSwapped(this, newMusic);
        }
    }

    public void loop() {
        this.loop(1.0f, 1.0f);
    }

    public void play() {
        this.play(1.0f, 1.0f);
    }

    public void play(float pitch, float volume) {
        this.startMusic(pitch, volume, false);
    }

    public void loop(float pitch, float volume) {
        this.startMusic(pitch, volume, true);
    }

    private void startMusic(float pitch, float volume, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.fireMusicSwapped(this);
        }
        currentMusic = this;
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        this.sound.playAsMusic(pitch, volume, loop);
        this.playing = true;
        this.setVolume(volume);
        if (this.requiredPosition != -1.0f) {
            this.setPosition(this.requiredPosition);
        }
    }

    public void pause() {
        this.playing = false;
        AudioImpl.pauseMusic();
    }

    public void stop() {
        this.playing = false;
        this.sound.stop();
    }

    public void resume() {
        this.playing = true;
        AudioImpl.restartMusic();
    }

    public boolean playing() {
        return currentMusic == this && this.playing;
    }

    public void setVolume(float volume) {
        if (volume > 1.0f) {
            volume = 1.0f;
        } else if (volume < 0.0f) {
            volume = 0.0f;
        }
        this.volume = volume;
        if (currentMusic == this) {
            SoundStore.get().setCurrentMusicVolume(volume);
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public void fade(int duration, float endVolume, boolean stopAfterFade) {
        this.stopAfterFade = stopAfterFade;
        this.fadeStartGain = this.volume;
        this.fadeEndGain = endVolume;
        this.fadeDuration = duration;
        this.fadeTime = duration;
    }

    void update(int delta) {
        if (!this.playing) {
            return;
        }
        if (this.fadeTime > 0) {
            this.fadeTime -= delta;
            if (this.fadeTime <= 0) {
                this.fadeTime = 0;
                if (this.stopAfterFade) {
                    this.stop();
                    return;
                }
            }
            float offset = (this.fadeEndGain - this.fadeStartGain) * (1.0f - (float)this.fadeTime / (float)this.fadeDuration);
            this.setVolume(this.fadeStartGain + offset);
        }
    }

    public boolean setPosition(float position) {
        if (this.playing) {
            this.requiredPosition = -1.0f;
            this.positioning = true;
            this.playing = false;
            boolean result = this.sound.setPosition(position);
            this.playing = true;
            this.positioning = false;
            return result;
        }
        this.requiredPosition = position;
        return false;
    }

    public float getPosition() {
        return this.sound.getPosition();
    }
}

