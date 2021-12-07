/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.LWJGLUtil
 */
package org.newdawn.slick.openal;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.lwjgl.LWJGLUtil;

public class AiffData {
    public final ByteBuffer data;
    public final int format;
    public final int samplerate;

    private AiffData(ByteBuffer data, int format, int samplerate) {
        this.data = data;
        this.format = format;
        this.samplerate = samplerate;
    }

    public void dispose() {
        this.data.clear();
    }

    public static AiffData create(URL path) {
        try {
            return AiffData.create(AudioSystem.getAudioInputStream(new BufferedInputStream(path.openStream())));
        }
        catch (Exception e) {
            LWJGLUtil.log((CharSequence)("Unable to create from: " + path));
            e.printStackTrace();
            return null;
        }
    }

    public static AiffData create(String path) {
        return AiffData.create(AiffData.class.getClassLoader().getResource(path));
    }

    public static AiffData create(InputStream is) {
        try {
            return AiffData.create(AudioSystem.getAudioInputStream(is));
        }
        catch (Exception e) {
            LWJGLUtil.log((CharSequence)"Unable to create from inputstream");
            e.printStackTrace();
            return null;
        }
    }

    public static AiffData create(byte[] buffer) {
        try {
            return AiffData.create(AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer))));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AiffData create(ByteBuffer buffer) {
        try {
            byte[] bytes = null;
            if (buffer.hasArray()) {
                bytes = buffer.array();
            } else {
                bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
            }
            return AiffData.create(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static AiffData create(AudioInputStream ais) {
        AudioFormat audioformat = ais.getFormat();
        int channels = 0;
        if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = 4352;
            } else {
                if (audioformat.getSampleSizeInBits() != 16) throw new RuntimeException("Illegal sample size");
                channels = 4353;
            }
        } else {
            if (audioformat.getChannels() != 2) throw new RuntimeException("Only mono or stereo is supported");
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = 4354;
            } else {
                if (audioformat.getSampleSizeInBits() != 16) throw new RuntimeException("Illegal sample size");
                channels = 4355;
            }
        }
        byte[] buf = new byte[audioformat.getChannels() * (int)ais.getFrameLength() * audioformat.getSampleSizeInBits() / 8];
        int read = 0;
        try {
            for (int total = 0; (read = ais.read(buf, total, buf.length - total)) != -1 && total < buf.length; total += read) {
            }
        }
        catch (IOException ioe) {
            return null;
        }
        ByteBuffer buffer = AiffData.convertAudioBytes(audioformat, buf, audioformat.getSampleSizeInBits() == 16);
        AiffData Aiffdata = new AiffData(buffer, channels, (int)audioformat.getSampleRate());
        try {
            ais.close();
            return Aiffdata;
        }
        catch (IOException ioe) {
            // empty catch block
        }
        return Aiffdata;
    }

    private static ByteBuffer convertAudioBytes(AudioFormat format, byte[] audio_bytes, boolean two_bytes_data) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audio_bytes);
        src.order(ByteOrder.BIG_ENDIAN);
        if (two_bytes_data) {
            ShortBuffer dest_short = dest.asShortBuffer();
            ShortBuffer src_short = src.asShortBuffer();
            while (src_short.hasRemaining()) {
                dest_short.put(src_short.get());
            }
        } else {
            while (src.hasRemaining()) {
                byte b = src.get();
                if (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
                    b = (byte)(b + 127);
                }
                dest.put(b);
            }
        }
        dest.rewind();
        return dest;
    }
}

