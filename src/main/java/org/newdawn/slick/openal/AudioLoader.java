/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.openal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;

public class AudioLoader {
    private static final String AIF = "AIF";
    private static final String WAV = "WAV";
    private static final String OGG = "OGG";
    private static final String MOD = "MOD";
    private static final String XM = "XM";
    private static boolean inited = false;

    private static void init() {
        if (!inited) {
            SoundStore.get().init();
            inited = true;
        }
    }

    public static Audio getAudio(String format, InputStream in) throws IOException {
        AudioLoader.init();
        if (format.equals(AIF)) {
            return SoundStore.get().getAIF(in);
        }
        if (format.equals(WAV)) {
            return SoundStore.get().getWAV(in);
        }
        if (format.equals(OGG)) {
            return SoundStore.get().getOgg(in);
        }
        throw new IOException("Unsupported format for non-streaming Audio: " + format);
    }

    public static Audio getStreamingAudio(String format, URL url) throws IOException {
        AudioLoader.init();
        if (format.equals(OGG)) {
            return SoundStore.get().getOggStream(url);
        }
        if (format.equals(MOD)) {
            return SoundStore.get().getMOD(url.openStream());
        }
        if (format.equals(XM)) {
            return SoundStore.get().getMOD(url.openStream());
        }
        throw new IOException("Unsupported format for streaming Audio: " + format);
    }

    public static void update() {
        AudioLoader.init();
        SoundStore.get().poll(0);
    }
}

