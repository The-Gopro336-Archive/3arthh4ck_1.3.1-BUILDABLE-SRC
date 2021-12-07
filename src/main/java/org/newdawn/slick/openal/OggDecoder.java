/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.openal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggInputStream;

public class OggDecoder {
    private int convsize = 16384;
    private byte[] convbuffer = new byte[this.convsize];

    public OggData getData(InputStream input) throws IOException {
        if (input == null) {
            throw new IOException("Failed to read OGG, source does not exist?");
        }
        ByteArrayOutputStream dataout = new ByteArrayOutputStream();
        OggInputStream oggInput = new OggInputStream(input);
        boolean done = false;
        while (!oggInput.atEnd()) {
            dataout.write(oggInput.read());
        }
        OggData ogg = new OggData();
        ogg.channels = oggInput.getChannels();
        ogg.rate = oggInput.getRate();
        byte[] data = dataout.toByteArray();
        ogg.data = ByteBuffer.allocateDirect(data.length);
        ogg.data.put(data);
        ogg.data.rewind();
        return ogg;
    }
}

