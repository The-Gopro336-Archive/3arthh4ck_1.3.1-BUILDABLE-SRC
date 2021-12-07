/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.newdawn.slick.opengl.ImageData;

public interface LoadableImageData
extends ImageData {
    public void configureEdging(boolean var1);

    public ByteBuffer loadImage(InputStream var1) throws IOException;

    public ByteBuffer loadImage(InputStream var1, boolean var2, int[] var3) throws IOException;

    public ByteBuffer loadImage(InputStream var1, boolean var2, boolean var3, int[] var4) throws IOException;
}

