/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PNGDecoder {
    public static Format ALPHA = new Format(1, true);
    public static Format LUMINANCE = new Format(1, false);
    public static Format LUMINANCE_ALPHA = new Format(2, true);
    public static Format RGB = new Format(3, false);
    public static Format RGBA = new Format(4, true);
    public static Format BGRA = new Format(4, true);
    public static Format ABGR = new Format(4, true);
    private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static final int IHDR = 1229472850;
    private static final int PLTE = 1347179589;
    private static final int tRNS = 1951551059;
    private static final int IDAT = 1229209940;
    private static final int IEND = 1229278788;
    private static final byte COLOR_GREYSCALE = 0;
    private static final byte COLOR_TRUECOLOR = 2;
    private static final byte COLOR_INDEXED = 3;
    private static final byte COLOR_GREYALPHA = 4;
    private static final byte COLOR_TRUEALPHA = 6;
    private final InputStream input;
    private final CRC32 crc;
    private final byte[] buffer;
    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;
    private int width;
    private int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private byte[] palette;
    private byte[] paletteA;
    private byte[] transPixel;

    /*
     * Enabled aggressive block sorting
     */
    public PNGDecoder(InputStream input) throws IOException {
        this.input = input;
        this.crc = new CRC32();
        this.buffer = new byte[4096];
        this.readFully(this.buffer, 0, SIGNATURE.length);
        if (!PNGDecoder.checkSignature(this.buffer)) {
            throw new IOException("Not a valid PNG file");
        }
        this.openChunk(1229472850);
        this.readIHDR();
        this.closeChunk();
        block5: while (true) {
            this.openChunk();
            switch (this.chunkType) {
                case 1229209940: {
                    break block5;
                }
                case 1347179589: {
                    this.readPLTE();
                    break;
                }
                case 1951551059: {
                    this.readtRNS();
                }
            }
            this.closeChunk();
        }
        if (this.colorType == 3 && this.palette == null) {
            throw new IOException("Missing PLTE chunk");
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean hasAlpha() {
        return this.colorType == 6 || this.paletteA != null || this.transPixel != null;
    }

    public boolean isRGB() {
        return this.colorType == 6 || this.colorType == 2 || this.colorType == 3;
    }

    public Format decideTextureFormat(Format fmt) {
        switch (this.colorType) {
            case 2: {
                if (fmt == ABGR || fmt == RGBA || fmt == BGRA || fmt == RGB) {
                    return fmt;
                }
                return RGB;
            }
            case 6: {
                if (fmt == ABGR || fmt == RGBA || fmt == BGRA || fmt == RGB) {
                    return fmt;
                }
                return RGBA;
            }
            case 0: {
                if (fmt == LUMINANCE || fmt == ALPHA) {
                    return fmt;
                }
                return LUMINANCE;
            }
            case 4: {
                return LUMINANCE_ALPHA;
            }
            case 3: {
                if (fmt == ABGR || fmt == RGBA || fmt == BGRA) {
                    return fmt;
                }
                return RGBA;
            }
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void decode(ByteBuffer buffer, int stride, Format fmt) throws IOException {
        int offset = buffer.position();
        int lineSize = (this.width * this.bitdepth + 7) / 8 * this.bytesPerPixel;
        byte[] curLine = new byte[lineSize + 1];
        byte[] prevLine = new byte[lineSize + 1];
        byte[] palLine = this.bitdepth < 8 ? new byte[this.width + 1] : null;
        Inflater inflater = new Inflater();
        try {
            for (int y = 0; y < this.height; ++y) {
                this.readChunkUnzip(inflater, curLine, 0, curLine.length);
                this.unfilter(curLine, prevLine);
                buffer.position(offset + y * stride);
                switch (this.colorType) {
                    case 2: {
                        if (fmt == ABGR) {
                            this.copyRGBtoABGR(buffer, curLine);
                            break;
                        }
                        if (fmt == RGBA) {
                            this.copyRGBtoRGBA(buffer, curLine);
                            break;
                        }
                        if (fmt == BGRA) {
                            this.copyRGBtoBGRA(buffer, curLine);
                            break;
                        }
                        if (fmt == RGB) {
                            this.copy(buffer, curLine);
                            break;
                        }
                        throw new UnsupportedOperationException("Unsupported format for this image");
                    }
                    case 6: {
                        if (fmt == ABGR) {
                            this.copyRGBAtoABGR(buffer, curLine);
                            break;
                        }
                        if (fmt == RGBA) {
                            this.copy(buffer, curLine);
                            break;
                        }
                        if (fmt == BGRA) {
                            this.copyRGBAtoBGRA(buffer, curLine);
                            break;
                        }
                        if (fmt == RGB) {
                            this.copyRGBAtoRGB(buffer, curLine);
                            break;
                        }
                        throw new UnsupportedOperationException("Unsupported format for this image");
                    }
                    case 0: {
                        if (fmt == LUMINANCE || fmt == ALPHA) {
                            this.copy(buffer, curLine);
                            break;
                        }
                        throw new UnsupportedOperationException("Unsupported format for this image");
                    }
                    case 4: {
                        if (fmt == LUMINANCE_ALPHA) {
                            this.copy(buffer, curLine);
                            break;
                        }
                        throw new UnsupportedOperationException("Unsupported format for this image");
                    }
                    case 3: {
                        switch (this.bitdepth) {
                            case 8: {
                                palLine = curLine;
                                break;
                            }
                            case 4: {
                                this.expand4(curLine, palLine);
                                break;
                            }
                            case 2: {
                                this.expand2(curLine, palLine);
                                break;
                            }
                            case 1: {
                                this.expand1(curLine, palLine);
                                break;
                            }
                            default: {
                                throw new UnsupportedOperationException("Unsupported bitdepth for this image");
                            }
                        }
                        if (fmt == ABGR) {
                            this.copyPALtoABGR(buffer, palLine);
                            break;
                        }
                        if (fmt == RGBA) {
                            this.copyPALtoRGBA(buffer, palLine);
                            break;
                        }
                        if (fmt == BGRA) {
                            this.copyPALtoBGRA(buffer, palLine);
                            break;
                        }
                        throw new UnsupportedOperationException("Unsupported format for this image");
                    }
                    default: {
                        throw new UnsupportedOperationException("Not yet implemented");
                    }
                }
                byte[] tmp = curLine;
                curLine = prevLine;
                prevLine = tmp;
            }
        }
        finally {
            inflater.end();
        }
    }

    private void copy(ByteBuffer buffer, byte[] curLine) {
        buffer.put(curLine, 1, curLine.length - 1);
    }

    private void copyRGBtoABGR(ByteBuffer buffer, byte[] curLine) {
        if (this.transPixel != null) {
            byte tr = this.transPixel[1];
            byte tg = this.transPixel[3];
            byte tb = this.transPixel[5];
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                byte r = curLine[i];
                byte g = curLine[i + 1];
                byte b = curLine[i + 2];
                byte a = -1;
                if (r == tr && g == tg && b == tb) {
                    a = 0;
                }
                buffer.put(a).put(b).put(g).put(r);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                buffer.put((byte)-1).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
            }
        }
    }

    private void copyRGBtoRGBA(ByteBuffer buffer, byte[] curLine) {
        if (this.transPixel != null) {
            byte tr = this.transPixel[1];
            byte tg = this.transPixel[3];
            byte tb = this.transPixel[5];
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                byte r = curLine[i];
                byte g = curLine[i + 1];
                byte b = curLine[i + 2];
                byte a = -1;
                if (r == tr && g == tg && b == tb) {
                    a = 0;
                }
                buffer.put(r).put(g).put(b).put(a);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]).put((byte)-1);
            }
        }
    }

    private void copyRGBtoBGRA(ByteBuffer buffer, byte[] curLine) {
        if (this.transPixel != null) {
            byte tr = this.transPixel[1];
            byte tg = this.transPixel[3];
            byte tb = this.transPixel[5];
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                byte r = curLine[i];
                byte g = curLine[i + 1];
                byte b = curLine[i + 2];
                byte a = -1;
                if (r == tr && g == tg && b == tb) {
                    a = 0;
                }
                buffer.put(b).put(g).put(r).put(a);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; i += 3) {
                buffer.put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]).put((byte)-1);
            }
        }
    }

    private void copyRGBAtoABGR(ByteBuffer buffer, byte[] curLine) {
        int n = curLine.length;
        for (int i = 1; i < n; i += 4) {
            buffer.put(curLine[i + 3]).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
        }
    }

    private void copyRGBAtoBGRA(ByteBuffer buffer, byte[] curLine) {
        int n = curLine.length;
        for (int i = 1; i < n; i += 4) {
            buffer.put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i + 0]).put(curLine[i + 3]);
        }
    }

    private void copyRGBAtoRGB(ByteBuffer buffer, byte[] curLine) {
        int n = curLine.length;
        for (int i = 1; i < n; i += 4) {
            buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]);
        }
    }

    private void copyPALtoABGR(ByteBuffer buffer, byte[] curLine) {
        if (this.paletteA != null) {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = this.paletteA[idx];
                buffer.put(a).put(b).put(g).put(r);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = -1;
                buffer.put(a).put(b).put(g).put(r);
            }
        }
    }

    private void copyPALtoRGBA(ByteBuffer buffer, byte[] curLine) {
        if (this.paletteA != null) {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = this.paletteA[idx];
                buffer.put(r).put(g).put(b).put(a);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = -1;
                buffer.put(r).put(g).put(b).put(a);
            }
        }
    }

    private void copyPALtoBGRA(ByteBuffer buffer, byte[] curLine) {
        if (this.paletteA != null) {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = this.paletteA[idx];
                buffer.put(b).put(g).put(r).put(a);
            }
        } else {
            int n = curLine.length;
            for (int i = 1; i < n; ++i) {
                int idx = curLine[i] & 0xFF;
                byte r = this.palette[idx * 3 + 0];
                byte g = this.palette[idx * 3 + 1];
                byte b = this.palette[idx * 3 + 2];
                byte a = -1;
                buffer.put(b).put(g).put(r).put(a);
            }
        }
    }

    private void expand4(byte[] src, byte[] dst) {
        int n = dst.length;
        for (int i = 1; i < n; i += 2) {
            int val = src[1 + (i >> 1)] & 0xFF;
            switch (n - i) {
                default: {
                    dst[i + 1] = (byte)(val & 0xF);
                }
                case 1: 
            }
            dst[i] = (byte)(val >> 4);
        }
    }

    private void expand2(byte[] src, byte[] dst) {
        int n = dst.length;
        for (int i = 1; i < n; i += 4) {
            int val = src[1 + (i >> 2)] & 0xFF;
            switch (n - i) {
                default: {
                    dst[i + 3] = (byte)(val & 3);
                }
                case 3: {
                    dst[i + 2] = (byte)(val >> 2 & 3);
                }
                case 2: {
                    dst[i + 1] = (byte)(val >> 4 & 3);
                }
                case 1: 
            }
            dst[i] = (byte)(val >> 6);
        }
    }

    private void expand1(byte[] src, byte[] dst) {
        int n = dst.length;
        for (int i = 1; i < n; i += 8) {
            int val = src[1 + (i >> 3)] & 0xFF;
            switch (n - i) {
                default: {
                    dst[i + 7] = (byte)(val & 1);
                }
                case 7: {
                    dst[i + 6] = (byte)(val >> 1 & 1);
                }
                case 6: {
                    dst[i + 5] = (byte)(val >> 2 & 1);
                }
                case 5: {
                    dst[i + 4] = (byte)(val >> 3 & 1);
                }
                case 4: {
                    dst[i + 3] = (byte)(val >> 4 & 1);
                }
                case 3: {
                    dst[i + 2] = (byte)(val >> 5 & 1);
                }
                case 2: {
                    dst[i + 1] = (byte)(val >> 6 & 1);
                }
                case 1: 
            }
            dst[i] = (byte)(val >> 7);
        }
    }

    private void unfilter(byte[] curLine, byte[] prevLine) throws IOException {
        switch (curLine[0]) {
            case 0: {
                break;
            }
            case 1: {
                this.unfilterSub(curLine);
                break;
            }
            case 2: {
                this.unfilterUp(curLine, prevLine);
                break;
            }
            case 3: {
                this.unfilterAverage(curLine, prevLine);
                break;
            }
            case 4: {
                this.unfilterPaeth(curLine, prevLine);
                break;
            }
            default: {
                throw new IOException("invalide filter type in scanline: " + curLine[0]);
            }
        }
    }

    private void unfilterSub(byte[] curLine) {
        int bpp = this.bytesPerPixel;
        int n = curLine.length;
        for (int i = bpp + 1; i < n; ++i) {
            int n2 = i;
            curLine[n2] = (byte)(curLine[n2] + curLine[i - bpp]);
        }
    }

    private void unfilterUp(byte[] curLine, byte[] prevLine) {
        int bpp = this.bytesPerPixel;
        int n = curLine.length;
        for (int i = 1; i < n; ++i) {
            int n2 = i;
            curLine[n2] = (byte)(curLine[n2] + prevLine[i]);
        }
    }

    private void unfilterAverage(byte[] curLine, byte[] prevLine) {
        int i;
        int bpp = this.bytesPerPixel;
        for (i = 1; i <= bpp; ++i) {
            int n = i;
            curLine[n] = (byte)(curLine[n] + (byte)((prevLine[i] & 0xFF) >>> 1));
        }
        int n = curLine.length;
        while (i < n) {
            int n2 = i;
            curLine[n2] = (byte)(curLine[n2] + (byte)((prevLine[i] & 0xFF) + (curLine[i - bpp] & 0xFF) >>> 1));
            ++i;
        }
    }

    private void unfilterPaeth(byte[] curLine, byte[] prevLine) {
        int i;
        int bpp = this.bytesPerPixel;
        for (i = 1; i <= bpp; ++i) {
            int n = i;
            curLine[n] = (byte)(curLine[n] + prevLine[i]);
        }
        int n = curLine.length;
        while (i < n) {
            int pc;
            int pb;
            int a = curLine[i - bpp] & 0xFF;
            int b = prevLine[i] & 0xFF;
            int c = prevLine[i - bpp] & 0xFF;
            int p = a + b - c;
            int pa = p - a;
            if (pa < 0) {
                pa = -pa;
            }
            if ((pb = p - b) < 0) {
                pb = -pb;
            }
            if ((pc = p - c) < 0) {
                pc = -pc;
            }
            if (pa <= pb && pa <= pc) {
                c = a;
            } else if (pb <= pc) {
                c = b;
            }
            int n2 = i++;
            curLine[n2] = (byte)(curLine[n2] + (byte)c);
        }
    }

    private void readIHDR() throws IOException {
        this.checkChunkLength(13);
        this.readChunk(this.buffer, 0, 13);
        this.width = this.readInt(this.buffer, 0);
        this.height = this.readInt(this.buffer, 4);
        this.bitdepth = this.buffer[8] & 0xFF;
        this.colorType = this.buffer[9] & 0xFF;
        block0 : switch (this.colorType) {
            case 0: {
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }
                this.bytesPerPixel = 1;
                break;
            }
            case 4: {
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }
                this.bytesPerPixel = 2;
                break;
            }
            case 2: {
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }
                this.bytesPerPixel = 3;
                break;
            }
            case 6: {
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }
                this.bytesPerPixel = 4;
                break;
            }
            case 3: {
                switch (this.bitdepth) {
                    case 1: 
                    case 2: 
                    case 4: 
                    case 8: {
                        this.bytesPerPixel = 1;
                        break block0;
                    }
                }
                throw new IOException("Unsupported bit depth: " + this.bitdepth);
            }
            default: {
                throw new IOException("unsupported color format: " + this.colorType);
            }
        }
        if (this.buffer[10] != 0) {
            throw new IOException("unsupported compression method");
        }
        if (this.buffer[11] != 0) {
            throw new IOException("unsupported filtering method");
        }
        if (this.buffer[12] != 0) {
            throw new IOException("unsupported interlace method");
        }
    }

    private void readPLTE() throws IOException {
        int paletteEntries = this.chunkLength / 3;
        if (paletteEntries < 1 || paletteEntries > 256 || this.chunkLength % 3 != 0) {
            throw new IOException("PLTE chunk has wrong length");
        }
        this.palette = new byte[paletteEntries * 3];
        this.readChunk(this.palette, 0, this.palette.length);
    }

    private void readtRNS() throws IOException {
        switch (this.colorType) {
            case 0: {
                this.checkChunkLength(2);
                this.transPixel = new byte[2];
                this.readChunk(this.transPixel, 0, 2);
                break;
            }
            case 2: {
                this.checkChunkLength(6);
                this.transPixel = new byte[6];
                this.readChunk(this.transPixel, 0, 6);
                break;
            }
            case 3: {
                if (this.palette == null) {
                    throw new IOException("tRNS chunk without PLTE chunk");
                }
                this.paletteA = new byte[this.palette.length / 3];
                Arrays.fill(this.paletteA, (byte)-1);
                this.readChunk(this.paletteA, 0, this.paletteA.length);
                break;
            }
        }
    }

    private void closeChunk() throws IOException {
        if (this.chunkRemaining > 0) {
            this.skip(this.chunkRemaining + 4);
        } else {
            this.readFully(this.buffer, 0, 4);
            int expectedCrc = this.readInt(this.buffer, 0);
            int computedCrc = (int)this.crc.getValue();
            if (computedCrc != expectedCrc) {
                throw new IOException("Invalid CRC");
            }
        }
        this.chunkRemaining = 0;
        this.chunkLength = 0;
        this.chunkType = 0;
    }

    private void openChunk() throws IOException {
        this.readFully(this.buffer, 0, 8);
        this.chunkLength = this.readInt(this.buffer, 0);
        this.chunkType = this.readInt(this.buffer, 4);
        this.chunkRemaining = this.chunkLength;
        this.crc.reset();
        this.crc.update(this.buffer, 4, 4);
    }

    private void openChunk(int expected) throws IOException {
        this.openChunk();
        if (this.chunkType != expected) {
            throw new IOException("Expected chunk: " + Integer.toHexString(expected));
        }
    }

    private void checkChunkLength(int expected) throws IOException {
        if (this.chunkLength != expected) {
            throw new IOException("Chunk has wrong size");
        }
    }

    private int readChunk(byte[] buffer, int offset, int length) throws IOException {
        if (length > this.chunkRemaining) {
            length = this.chunkRemaining;
        }
        this.readFully(buffer, offset, length);
        this.crc.update(buffer, offset, length);
        this.chunkRemaining -= length;
        return length;
    }

    private void refillInflater(Inflater inflater) throws IOException {
        while (this.chunkRemaining == 0) {
            this.closeChunk();
            this.openChunk(1229209940);
        }
        int read = this.readChunk(this.buffer, 0, this.buffer.length);
        inflater.setInput(this.buffer, 0, read);
    }

    private void readChunkUnzip(Inflater inflater, byte[] buffer, int offset, int length) throws IOException {
        try {
            do {
                int read;
                if ((read = inflater.inflate(buffer, offset, length)) <= 0) {
                    if (inflater.finished()) {
                        throw new EOFException();
                    }
                    if (inflater.needsInput()) {
                        this.refillInflater(inflater);
                        continue;
                    }
                    throw new IOException("Can't inflate " + length + " bytes");
                }
                offset += read;
                length -= read;
            } while (length > 0);
        }
        catch (DataFormatException ex) {
            throw (IOException)new IOException("inflate error").initCause(ex);
        }
    }

    private void readFully(byte[] buffer, int offset, int length) throws IOException {
        int read;
        do {
            if ((read = this.input.read(buffer, offset, length)) < 0) {
                throw new EOFException();
            }
            offset += read;
        } while ((length -= read) > 0);
    }

    private int readInt(byte[] buffer, int offset) {
        return buffer[offset] << 24 | (buffer[offset + 1] & 0xFF) << 16 | (buffer[offset + 2] & 0xFF) << 8 | buffer[offset + 3] & 0xFF;
    }

    private void skip(long amount) throws IOException {
        while (amount > 0L) {
            long skipped = this.input.skip(amount);
            if (skipped < 0L) {
                throw new EOFException();
            }
            amount -= skipped;
        }
    }

    private static boolean checkSignature(byte[] buffer) {
        for (int i = 0; i < SIGNATURE.length; ++i) {
            if (buffer[i] == SIGNATURE[i]) continue;
            return false;
        }
        return true;
    }

    public static class Format {
        final int numComponents;
        final boolean hasAlpha;

        private Format(int numComponents, boolean hasAlpha) {
            this.numComponents = numComponents;
            this.hasAlpha = hasAlpha;
        }

        public int getNumComponents() {
            return this.numComponents;
        }

        public boolean isHasAlpha() {
            return this.hasAlpha;
        }
    }
}

