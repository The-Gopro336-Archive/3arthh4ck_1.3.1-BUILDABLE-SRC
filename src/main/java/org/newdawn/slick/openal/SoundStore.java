/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.Sys
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.OpenALException
 */
package org.newdawn.slick.openal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.openal.AiffData;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioImpl;
import org.newdawn.slick.openal.DeferredSound;
import org.newdawn.slick.openal.MODSound;
import org.newdawn.slick.openal.NullAudio;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;
import org.newdawn.slick.openal.OpenALStreamPlayer;
import org.newdawn.slick.openal.StreamSound;
import org.newdawn.slick.openal.WaveData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class SoundStore {
    private static SoundStore store = new SoundStore();
    private boolean sounds;
    private boolean music;
    private boolean soundWorks;
    private int sourceCount;
    private HashMap loaded = new HashMap();
    private int currentMusic = -1;
    private IntBuffer sources;
    private int nextSource;
    private boolean inited = false;
    private MODSound mod;
    private OpenALStreamPlayer stream;
    private float musicVolume = 1.0f;
    private float soundVolume = 1.0f;
    private float lastCurrentMusicVolume = 1.0f;
    private boolean paused;
    private boolean deferred;
    private FloatBuffer sourceVel = BufferUtils.createFloatBuffer((int)3).put(new float[]{0.0f, 0.0f, 0.0f});
    private FloatBuffer sourcePos = BufferUtils.createFloatBuffer((int)3);
    private int maxSources = 64;

    private SoundStore() {
    }

    public void clear() {
        store = new SoundStore();
    }

    public void disable() {
        this.inited = true;
    }

    public void setDeferredLoading(boolean deferred) {
        this.deferred = deferred;
    }

    public boolean isDeferredLoading() {
        return this.deferred;
    }

    public void setMusicOn(boolean music) {
        if (this.soundWorks) {
            this.music = music;
            if (music) {
                this.restartLoop();
                this.setMusicVolume(this.musicVolume);
            } else {
                this.pauseLoop();
            }
        }
    }

    public boolean isMusicOn() {
        return this.music;
    }

    public void setMusicVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        this.musicVolume = volume;
        if (this.soundWorks) {
            AL10.alSourcef((int)this.sources.get(0), (int)4106, (float)(this.lastCurrentMusicVolume * this.musicVolume));
        }
    }

    public float getCurrentMusicVolume() {
        return this.lastCurrentMusicVolume;
    }

    public void setCurrentMusicVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        if (this.soundWorks) {
            this.lastCurrentMusicVolume = volume;
            AL10.alSourcef((int)this.sources.get(0), (int)4106, (float)(this.lastCurrentMusicVolume * this.musicVolume));
        }
    }

    public void setSoundVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        this.soundVolume = volume;
    }

    public boolean soundWorks() {
        return this.soundWorks;
    }

    public boolean musicOn() {
        return this.music;
    }

    public float getSoundVolume() {
        return this.soundVolume;
    }

    public float getMusicVolume() {
        return this.musicVolume;
    }

    public int getSource(int index) {
        if (!this.soundWorks) {
            return -1;
        }
        if (index < 0) {
            return -1;
        }
        return this.sources.get(index);
    }

    public void setSoundsOn(boolean sounds) {
        if (this.soundWorks) {
            this.sounds = sounds;
        }
    }

    public boolean soundsOn() {
        return this.sounds;
    }

    public void setMaxSources(int max) {
        this.maxSources = max;
    }

    public void init() {
        if (this.inited) {
            return;
        }
        Log.info("Initialising sounds..");
        this.inited = true;
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    AL.create();
                    SoundStore.this.soundWorks = true;
                    SoundStore.this.sounds = true;
                    SoundStore.this.music = true;
                    Log.info("- Sound works");
                }
                catch (Exception e) {
                    Log.error("Sound initialisation failure.");
                    Log.error(e);
                    SoundStore.this.soundWorks = false;
                    SoundStore.this.sounds = false;
                    SoundStore.this.music = false;
                }
                return null;
            }
        });
        if (this.soundWorks) {
            this.sourceCount = 0;
            this.sources = BufferUtils.createIntBuffer((int)this.maxSources);
            while (AL10.alGetError() == 0) {
                IntBuffer temp = BufferUtils.createIntBuffer((int)1);
                try {
                    AL10.alGenSources((IntBuffer)temp);
                    if (AL10.alGetError() != 0) continue;
                    ++this.sourceCount;
                    this.sources.put(temp.get(0));
                    if (this.sourceCount <= this.maxSources - 1) continue;
                }
                catch (OpenALException e) {}
                break;
            }
            Log.info("- " + this.sourceCount + " OpenAL source available");
            if (AL10.alGetError() != 0) {
                this.sounds = false;
                this.music = false;
                this.soundWorks = false;
                Log.error("- AL init failed");
            } else {
                FloatBuffer listenerOri = BufferUtils.createFloatBuffer((int)6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
                FloatBuffer listenerVel = BufferUtils.createFloatBuffer((int)3).put(new float[]{0.0f, 0.0f, 0.0f});
                FloatBuffer listenerPos = BufferUtils.createFloatBuffer((int)3).put(new float[]{0.0f, 0.0f, 0.0f});
                listenerPos.flip();
                listenerVel.flip();
                listenerOri.flip();
                AL10.alListener((int)4100, (FloatBuffer)listenerPos);
                AL10.alListener((int)4102, (FloatBuffer)listenerVel);
                AL10.alListener((int)4111, (FloatBuffer)listenerOri);
                Log.info("- Sounds source generated");
            }
        }
    }

    void stopSource(int index) {
        AL10.alSourceStop((int)this.sources.get(index));
    }

    int playAsSound(int buffer, float pitch, float gain, boolean loop) {
        return this.playAsSoundAt(buffer, pitch, gain, loop, 0.0f, 0.0f, 0.0f);
    }

    int playAsSoundAt(int buffer, float pitch, float gain, boolean loop, float x, float y, float z) {
        if ((gain *= this.soundVolume) == 0.0f) {
            gain = 0.001f;
        }
        if (this.soundWorks && this.sounds) {
            int nextSource = this.findFreeSource();
            if (nextSource == -1) {
                return -1;
            }
            AL10.alSourceStop((int)this.sources.get(nextSource));
            AL10.alSourcei((int)this.sources.get(nextSource), (int)4105, (int)buffer);
            AL10.alSourcef((int)this.sources.get(nextSource), (int)4099, (float)pitch);
            AL10.alSourcef((int)this.sources.get(nextSource), (int)4106, (float)gain);
            AL10.alSourcei((int)this.sources.get(nextSource), (int)4103, (int)(loop ? 1 : 0));
            this.sourcePos.clear();
            this.sourceVel.clear();
            this.sourceVel.put(new float[]{0.0f, 0.0f, 0.0f});
            this.sourcePos.put(new float[]{x, y, z});
            this.sourcePos.flip();
            this.sourceVel.flip();
            AL10.alSource((int)this.sources.get(nextSource), (int)4100, (FloatBuffer)this.sourcePos);
            AL10.alSource((int)this.sources.get(nextSource), (int)4102, (FloatBuffer)this.sourceVel);
            AL10.alSourcePlay((int)this.sources.get(nextSource));
            return nextSource;
        }
        return -1;
    }

    boolean isPlaying(int index) {
        int state = AL10.alGetSourcei((int)this.sources.get(index), (int)4112);
        return state == 4114;
    }

    private int findFreeSource() {
        for (int i = 1; i < this.sourceCount - 1; ++i) {
            int state = AL10.alGetSourcei((int)this.sources.get(i), (int)4112);
            if (state == 4114 || state == 4115) continue;
            return i;
        }
        return -1;
    }

    void playAsMusic(int buffer, float pitch, float gain, boolean loop) {
        this.paused = false;
        this.setMOD(null);
        if (this.soundWorks) {
            if (this.currentMusic != -1) {
                AL10.alSourceStop((int)this.sources.get(0));
            }
            this.getMusicSource();
            AL10.alSourcei((int)this.sources.get(0), (int)4105, (int)buffer);
            AL10.alSourcef((int)this.sources.get(0), (int)4099, (float)pitch);
            AL10.alSourcei((int)this.sources.get(0), (int)4103, (int)(loop ? 1 : 0));
            this.currentMusic = this.sources.get(0);
            if (!this.music) {
                this.pauseLoop();
            } else {
                AL10.alSourcePlay((int)this.sources.get(0));
            }
        }
    }

    private int getMusicSource() {
        return this.sources.get(0);
    }

    public void setMusicPitch(float pitch) {
        if (this.soundWorks) {
            AL10.alSourcef((int)this.sources.get(0), (int)4099, (float)pitch);
        }
    }

    public void pauseLoop() {
        if (this.soundWorks && this.currentMusic != -1) {
            this.paused = true;
            AL10.alSourcePause((int)this.currentMusic);
        }
    }

    public void restartLoop() {
        if (this.music && this.soundWorks && this.currentMusic != -1) {
            this.paused = false;
            AL10.alSourcePlay((int)this.currentMusic);
        }
    }

    boolean isPlaying(OpenALStreamPlayer player) {
        return this.stream == player;
    }

    public Audio getMOD(String ref) throws IOException {
        return this.getMOD(ref, ResourceLoader.getResourceAsStream(ref));
    }

    public Audio getMOD(InputStream in) throws IOException {
        return this.getMOD(in.toString(), in);
    }

    public Audio getMOD(String ref, InputStream in) throws IOException {
        if (!this.soundWorks) {
            return new NullAudio();
        }
        if (!this.inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }
        if (this.deferred) {
            return new DeferredSound(ref, in, 3);
        }
        return new MODSound(this, in);
    }

    public Audio getAIF(String ref) throws IOException {
        return this.getAIF(ref, ResourceLoader.getResourceAsStream(ref));
    }

    public Audio getAIF(InputStream in) throws IOException {
        return this.getAIF(in.toString(), in);
    }

    public Audio getAIF(String ref, InputStream in) throws IOException {
        in = new BufferedInputStream(in);
        if (!this.soundWorks) {
            return new NullAudio();
        }
        if (!this.inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }
        if (this.deferred) {
            return new DeferredSound(ref, in, 4);
        }
        int buffer = -1;
        if (this.loaded.get(ref) != null) {
            buffer = (Integer)this.loaded.get(ref);
        } else {
            try {
                IntBuffer buf = BufferUtils.createIntBuffer((int)1);
                AiffData data = AiffData.create(in);
                AL10.alGenBuffers((IntBuffer)buf);
                AL10.alBufferData((int)buf.get(0), (int)data.format, (ByteBuffer)data.data, (int)data.samplerate);
                this.loaded.put(ref, new Integer(buf.get(0)));
                buffer = buf.get(0);
            }
            catch (Exception e) {
                Log.error(e);
                IOException x = new IOException("Failed to load: " + ref);
                x.initCause(e);
                throw x;
            }
        }
        if (buffer == -1) {
            throw new IOException("Unable to load: " + ref);
        }
        return new AudioImpl(this, buffer);
    }

    public Audio getWAV(String ref) throws IOException {
        return this.getWAV(ref, ResourceLoader.getResourceAsStream(ref));
    }

    public Audio getWAV(InputStream in) throws IOException {
        return this.getWAV(in.toString(), in);
    }

    public Audio getWAV(String ref, InputStream in) throws IOException {
        if (!this.soundWorks) {
            return new NullAudio();
        }
        if (!this.inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }
        if (this.deferred) {
            return new DeferredSound(ref, in, 2);
        }
        int buffer = -1;
        if (this.loaded.get(ref) != null) {
            buffer = (Integer)this.loaded.get(ref);
        } else {
            try {
                IntBuffer buf = BufferUtils.createIntBuffer((int)1);
                WaveData data = WaveData.create(in);
                AL10.alGenBuffers((IntBuffer)buf);
                AL10.alBufferData((int)buf.get(0), (int)data.format, (ByteBuffer)data.data, (int)data.samplerate);
                this.loaded.put(ref, new Integer(buf.get(0)));
                buffer = buf.get(0);
            }
            catch (Exception e) {
                Log.error(e);
                IOException x = new IOException("Failed to load: " + ref);
                x.initCause(e);
                throw x;
            }
        }
        if (buffer == -1) {
            throw new IOException("Unable to load: " + ref);
        }
        return new AudioImpl(this, buffer);
    }

    public Audio getOggStream(String ref) throws IOException {
        if (!this.soundWorks) {
            return new NullAudio();
        }
        this.setMOD(null);
        this.setStream(null);
        if (this.currentMusic != -1) {
            AL10.alSourceStop((int)this.sources.get(0));
        }
        this.getMusicSource();
        this.currentMusic = this.sources.get(0);
        return new StreamSound(new OpenALStreamPlayer(this.currentMusic, ref));
    }

    public Audio getOggStream(URL ref) throws IOException {
        if (!this.soundWorks) {
            return new NullAudio();
        }
        this.setMOD(null);
        this.setStream(null);
        if (this.currentMusic != -1) {
            AL10.alSourceStop((int)this.sources.get(0));
        }
        this.getMusicSource();
        this.currentMusic = this.sources.get(0);
        return new StreamSound(new OpenALStreamPlayer(this.currentMusic, ref));
    }

    public Audio getOgg(String ref) throws IOException {
        return this.getOgg(ref, ResourceLoader.getResourceAsStream(ref));
    }

    public Audio getOgg(InputStream in) throws IOException {
        return this.getOgg(in.toString(), in);
    }

    public Audio getOgg(String ref, InputStream in) throws IOException {
        if (!this.soundWorks) {
            return new NullAudio();
        }
        if (!this.inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }
        if (this.deferred) {
            return new DeferredSound(ref, in, 1);
        }
        int buffer = -1;
        if (this.loaded.get(ref) != null) {
            buffer = (Integer)this.loaded.get(ref);
        } else {
            try {
                IntBuffer buf = BufferUtils.createIntBuffer((int)1);
                OggDecoder decoder = new OggDecoder();
                OggData ogg = decoder.getData(in);
                AL10.alGenBuffers((IntBuffer)buf);
                AL10.alBufferData((int)buf.get(0), (int)(ogg.channels > 1 ? 4355 : 4353), (ByteBuffer)ogg.data, (int)ogg.rate);
                this.loaded.put(ref, new Integer(buf.get(0)));
                buffer = buf.get(0);
            }
            catch (Exception e) {
                Log.error(e);
                Sys.alert((String)"Error", (String)("Failed to load: " + ref + " - " + e.getMessage()));
                throw new IOException("Unable to load: " + ref);
            }
        }
        if (buffer == -1) {
            throw new IOException("Unable to load: " + ref);
        }
        return new AudioImpl(this, buffer);
    }

    void setMOD(MODSound sound) {
        if (!this.soundWorks) {
            return;
        }
        this.currentMusic = this.sources.get(0);
        this.stopSource(0);
        this.mod = sound;
        if (sound != null) {
            this.stream = null;
        }
        this.paused = false;
    }

    void setStream(OpenALStreamPlayer stream) {
        if (!this.soundWorks) {
            return;
        }
        this.currentMusic = this.sources.get(0);
        this.stream = stream;
        if (stream != null) {
            this.mod = null;
        }
        this.paused = false;
    }

    public void poll(int delta) {
        if (!this.soundWorks) {
            return;
        }
        if (this.paused) {
            return;
        }
        if (this.music) {
            if (this.mod != null) {
                try {
                    this.mod.poll();
                }
                catch (OpenALException e) {
                    Log.error("Error with OpenGL MOD Player on this this platform");
                    Log.error(e);
                    this.mod = null;
                }
            }
            if (this.stream != null) {
                try {
                    this.stream.update();
                }
                catch (OpenALException e) {
                    Log.error("Error with OpenGL Streaming Player on this this platform");
                    Log.error(e);
                    this.mod = null;
                }
            }
        }
    }

    public boolean isMusicPlaying() {
        if (!this.soundWorks) {
            return false;
        }
        int state = AL10.alGetSourcei((int)this.sources.get(0), (int)4112);
        return state == 4114 || state == 4115;
    }

    public static SoundStore get() {
        return store;
    }

    public void stopSoundEffect(int id) {
        AL10.alSourceStop((int)id);
    }

    public int getSourceCount() {
        return this.sourceCount;
    }
}

