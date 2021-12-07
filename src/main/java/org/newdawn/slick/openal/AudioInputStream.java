/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.openal;

import java.io.IOException;

interface AudioInputStream {
    public int getChannels();

    public int getRate();

    public int read() throws IOException;

    public int read(byte[] var1) throws IOException;

    public int read(byte[] var1, int var2, int var3) throws IOException;

    public boolean atEnd();

    public void close() throws IOException;
}

