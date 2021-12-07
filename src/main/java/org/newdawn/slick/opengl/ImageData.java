/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;

public interface ImageData {
    public int getDepth();

    public int getWidth();

    public int getHeight();

    public int getTexWidth();

    public int getTexHeight();

    public ByteBuffer getImageBufferData();
}

