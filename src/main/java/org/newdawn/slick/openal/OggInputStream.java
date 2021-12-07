package org.newdawn.slick.openal;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.openal.AudioInputStream;
import org.newdawn.slick.util.Log;

public class OggInputStream
extends InputStream
implements AudioInputStream {
    private int convsize = 16384;
    private byte[] convbuffer = new byte[this.convsize];
    private InputStream input;
    private Info oggInfo = new Info();
    private boolean endOfStream;
    private SyncState syncState = new SyncState();
    private StreamState streamState = new StreamState();
    private Page page = new Page();
    private Packet packet = new Packet();
    private Comment comment = new Comment();
    private DspState dspState = new DspState();
    private Block vorbisBlock = new Block(this.dspState);
    byte[] buffer;
    int bytes = 0;
    boolean bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
    boolean endOfBitStream = true;
    boolean inited = false;
    private int readIndex;
    private ByteBuffer pcmBuffer = BufferUtils.createByteBuffer(2048000);
    private int total;

    public OggInputStream(InputStream input) throws IOException {
        this.input = input;
        this.total = input.available();
        this.init();
    }

    public int getLength() {
        return this.total;
    }

    @Override
    public int getChannels() {
        return this.oggInfo.channels;
    }

    @Override
    public int getRate() {
        return this.oggInfo.rate;
    }

    private void init() throws IOException {
        this.initVorbis();
        this.readPCM();
    }

    @Override
    public int available() {
        return this.endOfStream ? 0 : 1;
    }

    private void initVorbis() {
        this.syncState.init();
    }

    private boolean getPageAndPacket() {
        int index = this.syncState.buffer(4096);
        this.buffer = this.syncState.data;
        if (this.buffer == null) {
            this.endOfStream = true;
            return false;
        }
        try {
            this.bytes = this.input.read(this.buffer, index, 4096);
        }
        catch (Exception e) {
            Log.error("Failure reading in vorbis");
            Log.error(e);
            this.endOfStream = true;
            return false;
        }
        this.syncState.wrote(this.bytes);
        if (this.syncState.pageout(this.page) != 1) {
            if (this.bytes < 4096) {
                return false;
            }
            Log.error("Input does not appear to be an Ogg bitstream.");
            this.endOfStream = true;
            return false;
        }
        this.streamState.init(this.page.serialno());
        this.oggInfo.init();
        this.comment.init();
        if (this.streamState.pagein(this.page) < 0) {
            Log.error("Error reading first page of Ogg bitstream data.");
            this.endOfStream = true;
            return false;
        }
        if (this.streamState.packetout(this.packet) != 1) {
            Log.error("Error reading initial header packet.");
            this.endOfStream = true;
            return false;
        }
        if (this.oggInfo.synthesis_headerin(this.comment, this.packet) < 0) {
            Log.error("This Ogg bitstream does not contain Vorbis audio data.");
            this.endOfStream = true;
            return false;
        }
        int i = 0;
        while (i < 2) {
            int result;
            while (i < 2 && (result = this.syncState.pageout(this.page)) != 0) {
                if (result != 1) continue;
                this.streamState.pagein(this.page);
                while (i < 2 && (result = this.streamState.packetout(this.packet)) != 0) {
                    if (result == -1) {
                        Log.error("Corrupt secondary header.  Exiting.");
                        this.endOfStream = true;
                        return false;
                    }
                    this.oggInfo.synthesis_headerin(this.comment, this.packet);
                    ++i;
                }
            }
            index = this.syncState.buffer(4096);
            this.buffer = this.syncState.data;
            try {
                this.bytes = this.input.read(this.buffer, index, 4096);
            }
            catch (Exception e) {
                Log.error("Failed to read Vorbis: ");
                Log.error(e);
                this.endOfStream = true;
                return false;
            }
            if (this.bytes == 0 && i < 2) {
                Log.error("End of file before finding all Vorbis headers!");
                this.endOfStream = true;
                return false;
            }
            this.syncState.wrote(this.bytes);
        }
        this.convsize = 4096 / this.oggInfo.channels;
        this.dspState.synthesis_init(this.oggInfo);
        this.vorbisBlock.init(this.dspState);
        return true;
    }

    private void readPCM() throws IOException {
        boolean wrote = false;
        while (true) {
            if (this.endOfBitStream) {
                if (!this.getPageAndPacket()) break;
                this.endOfBitStream = false;
            }
            if (!this.inited) {
                this.inited = true;
                return;
            }
            float[][][] _pcm = new float[1][][];
            int[] _index = new int[this.oggInfo.channels];
            while (!this.endOfBitStream) {
                int result;
                while (!this.endOfBitStream && (result = this.syncState.pageout(this.page)) != 0) {
                    if (result == -1) {
                        Log.error("Corrupt or missing data in bitstream; continuing...");
                        continue;
                    }
                    this.streamState.pagein(this.page);
                    while ((result = this.streamState.packetout(this.packet)) != 0) {
                        int samples;
                        if (result == -1) continue;
                        if (this.vorbisBlock.synthesis(this.packet) == 0) {
                            this.dspState.synthesis_blockin(this.vorbisBlock);
                        }
                        while ((samples = this.dspState.synthesis_pcmout(_pcm, _index)) > 0) {
                            float[][] pcm = _pcm[0];
                            int bout = samples < this.convsize ? samples : this.convsize;
                            for (int i = 0; i < this.oggInfo.channels; ++i) {
                                int ptr = i * 2;
                                int mono = _index[i];
                                for (int j = 0; j < bout; ++j) {
                                    int val = (int)((double)pcm[i][mono + j] * 32767.0);
                                    if (val > Short.MAX_VALUE) {
                                        val = Short.MAX_VALUE;
                                    }
                                    if (val < Short.MIN_VALUE) {
                                        val = Short.MIN_VALUE;
                                    }
                                    if (val < 0) {
                                        val |= 0x8000;
                                    }
                                    if (this.bigEndian) {
                                        this.convbuffer[ptr] = (byte)(val >>> 8);
                                        this.convbuffer[ptr + 1] = (byte)val;
                                    } else {
                                        this.convbuffer[ptr] = (byte)val;
                                        this.convbuffer[ptr + 1] = (byte)(val >>> 8);
                                    }
                                    ptr += 2 * this.oggInfo.channels;
                                }
                            }
                            int bytesToWrite = 2 * this.oggInfo.channels * bout;
                            if (bytesToWrite >= this.pcmBuffer.remaining()) {
                                Log.warn("Read block from OGG that was too big to be buffered: " + bytesToWrite);
                            } else {
                                this.pcmBuffer.put(this.convbuffer, 0, bytesToWrite);
                            }
                            wrote = true;
                            this.dspState.synthesis_read(bout);
                        }
                    }
                    if (this.page.eos() != 0) {
                        this.endOfBitStream = true;
                    }
                    if (this.endOfBitStream || !wrote) continue;
                    return;
                }
                if (this.endOfBitStream) continue;
                this.bytes = 0;
                int index = this.syncState.buffer(4096);
                if (index >= 0) {
                    this.buffer = this.syncState.data;
                    try {
                        this.bytes = this.input.read(this.buffer, index, 4096);
                    }
                    catch (Exception e) {
                        Log.error("Failure during vorbis decoding");
                        Log.error(e);
                        this.endOfStream = true;
                        return;
                    }
                } else {
                    this.bytes = 0;
                }
                this.syncState.wrote(this.bytes);
                if (this.bytes != 0) continue;
                this.endOfBitStream = true;
            }
            this.streamState.clear();
            this.vorbisBlock.clear();
            this.dspState.clear();
            this.oggInfo.clear();
        }
        this.syncState.clear();
        this.endOfStream = true;
    }

    @Override
    public int read() throws IOException {
        if (this.readIndex >= this.pcmBuffer.position()) {
            this.pcmBuffer.clear();
            this.readPCM();
            this.readIndex = 0;
        }
        if (this.readIndex >= this.pcmBuffer.position()) {
            return -1;
        }
        int value = this.pcmBuffer.get(this.readIndex);
        if (value < 0) {
            value = 256 + value;
        }
        ++this.readIndex;
        return value;
    }

    @Override
    public boolean atEnd() {
        return this.endOfStream && this.readIndex >= this.pcmBuffer.position();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            try {
                int value = this.read();
                if (value < 0) {
                    if (i == 0) {
                        return -1;
                    }
                    return i;
                }
                b[i] = (byte)value;
                continue;
            }
            catch (IOException e) {
                Log.error(e);
                return i;
            }
        }
        return len;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
    }
}
