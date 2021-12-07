package org.newdawn.slick.openal;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.openal.AudioInputStream;
import org.newdawn.slick.openal.OggInputStream;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class OpenALStreamPlayer {
    public static final int BUFFER_COUNT = 3;
    private static final int sectionSize = 81920;
    private byte[] buffer = new byte[81920];
    private IntBuffer bufferNames;
    private ByteBuffer bufferData = BufferUtils.createByteBuffer(81920);
    private IntBuffer unqueued = BufferUtils.createIntBuffer(1);
    private int source;
    private int remainingBufferCount;
    private boolean loop;
    private boolean done = true;
    private AudioInputStream audio;
    private String ref;
    private URL url;
    private float pitch;
    private float positionOffset;

    public OpenALStreamPlayer(int source, String ref) {
        this.source = source;
        this.ref = ref;
        this.bufferNames = BufferUtils.createIntBuffer(3);
        AL10.alGenBuffers(this.bufferNames);
    }

    public OpenALStreamPlayer(int source, URL url) {
        this.source = source;
        this.url = url;
        this.bufferNames = BufferUtils.createIntBuffer(3);
        AL10.alGenBuffers(this.bufferNames);
    }

    private void initStreams() throws IOException {
        if (this.audio != null) {
            this.audio.close();
        }
        OggInputStream audio = this.url != null ? new OggInputStream(this.url.openStream()) : new OggInputStream(ResourceLoader.getResourceAsStream(this.ref));
        this.audio = audio;
        this.positionOffset = 0.0f;
    }

    public String getSource() {
        return this.url == null ? this.ref : this.url.toString();
    }

    private void removeBuffers() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        for (int queued = AL10.alGetSourcei(this.source, 4117); queued > 0; --queued) {
            AL10.alSourceUnqueueBuffers(this.source, buffer);
        }
    }

    public void play(boolean loop) throws IOException {
        this.loop = loop;
        this.initStreams();
        this.done = false;
        AL10.alSourceStop(this.source);
        this.removeBuffers();
        this.startPlayback();
    }

    public void setup(float pitch) {
        this.pitch = pitch;
    }

    public boolean done() {
        return this.done;
    }

    public void update() {
        if (this.done) {
            return;
        }
        float sampleRate = this.audio.getRate();
        float sampleSize = this.audio.getChannels() > 1 ? 4.0f : 2.0f;
        for (int processed = AL10.alGetSourcei(this.source, 4118); processed > 0; --processed) {
            this.unqueued.clear();
            AL10.alSourceUnqueueBuffers(this.source, this.unqueued);
            int bufferIndex = this.unqueued.get(0);
            float bufferLength = (float)AL10.alGetBufferi(bufferIndex, 8196) / sampleSize / sampleRate;
            this.positionOffset += bufferLength;
            if (this.stream(bufferIndex)) {
                AL10.alSourceQueueBuffers(this.source, this.unqueued);
                continue;
            }
            --this.remainingBufferCount;
            if (this.remainingBufferCount != 0) continue;
            this.done = true;
        }
        int state = AL10.alGetSourcei(this.source, 4112);
        if (state != 4114) {
            AL10.alSourcePlay(this.source);
        }
    }

    public boolean stream(int bufferId) {
        try {
            int count = this.audio.read(this.buffer);
            if (count != -1) {
                this.bufferData.clear();
                this.bufferData.put(this.buffer, 0, count);
                this.bufferData.flip();
                int format = this.audio.getChannels() > 1 ? 4355 : 4353;
                try {
                    AL10.alBufferData(bufferId, format, this.bufferData, this.audio.getRate());
                }
                catch (OpenALException e) {
                    Log.error("Failed to loop buffer: " + bufferId + " " + format + " " + count + " " + this.audio.getRate(), e);
                    return false;
                }
            } else if (this.loop) {
                this.initStreams();
                this.stream(bufferId);
            } else {
                this.done = true;
                return false;
            }
            return true;
        }
        catch (IOException e) {
            Log.error(e);
            return false;
        }
    }

    public boolean setPosition(float position) {
        try {
            if (this.getPosition() > position) {
                this.initStreams();
            }
            float sampleRate = this.audio.getRate();
            float sampleSize = this.audio.getChannels() > 1 ? 4.0f : 2.0f;
            while (this.positionOffset < position) {
                int count = this.audio.read(this.buffer);
                if (count != -1) {
                    float bufferLength = (float)count / sampleSize / sampleRate;
                    this.positionOffset += bufferLength;
                    continue;
                }
                if (this.loop) {
                    this.initStreams();
                } else {
                    this.done = true;
                }
                return false;
            }
            this.startPlayback();
            return true;
        }
        catch (IOException e) {
            Log.error(e);
            return false;
        }
    }

    private void startPlayback() {
        AL10.alSourcei(this.source, 4103, 0);
        AL10.alSourcef(this.source, 4099, this.pitch);
        this.remainingBufferCount = 3;
        for (int i = 0; i < 3; ++i) {
            this.stream(this.bufferNames.get(i));
        }
        AL10.alSourceQueueBuffers(this.source, this.bufferNames);
        AL10.alSourcePlay(this.source);
    }

    public float getPosition() {
        return this.positionOffset + AL10.alGetSourcef(this.source, 4132);
    }
}
