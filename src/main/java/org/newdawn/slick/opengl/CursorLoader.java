/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.input.Cursor
 */
package org.newdawn.slick.opengl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.TGAImageData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class CursorLoader {
    private static CursorLoader single = new CursorLoader();

    public static CursorLoader get() {
        return single;
    }

    private CursorLoader() {
    }

    public Cursor getCursor(String ref, int x, int y) throws IOException, LWJGLException {
        LoadableImageData imageData = null;
        imageData = ImageDataFactory.getImageDataFor(ref);
        imageData.configureEdging(false);
        ByteBuffer buf = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), true, true, null);
        for (int i = 0; i < buf.limit(); i += 4) {
            byte red = buf.get(i);
            byte green = buf.get(i + 1);
            byte blue = buf.get(i + 2);
            byte alpha = buf.get(i + 3);
            buf.put(i + 2, red);
            buf.put(i + 1, green);
            buf.put(i, blue);
            buf.put(i + 3, alpha);
        }
        try {
            int yspot = imageData.getHeight() - y - 1;
            if (yspot < 0) {
                yspot = 0;
            }
            return new Cursor(imageData.getTexWidth(), imageData.getTexHeight(), x, yspot, 1, buf.asIntBuffer(), null);
        }
        catch (Throwable e) {
            Log.info("Chances are you cursor is too small for this platform");
            throw new LWJGLException(e);
        }
    }

    public Cursor getCursor(ByteBuffer buf, int x, int y, int width, int height) throws IOException, LWJGLException {
        for (int i = 0; i < buf.limit(); i += 4) {
            byte red = buf.get(i);
            byte green = buf.get(i + 1);
            byte blue = buf.get(i + 2);
            byte alpha = buf.get(i + 3);
            buf.put(i + 2, red);
            buf.put(i + 1, green);
            buf.put(i, blue);
            buf.put(i + 3, alpha);
        }
        try {
            int yspot = height - y - 1;
            if (yspot < 0) {
                yspot = 0;
            }
            return new Cursor(width, height, x, yspot, 1, buf.asIntBuffer(), null);
        }
        catch (Throwable e) {
            Log.info("Chances are you cursor is too small for this platform");
            throw new LWJGLException(e);
        }
    }

    public Cursor getCursor(ImageData imageData, int x, int y) throws IOException, LWJGLException {
        ByteBuffer buf = imageData.getImageBufferData();
        for (int i = 0; i < buf.limit(); i += 4) {
            byte red = buf.get(i);
            byte green = buf.get(i + 1);
            byte blue = buf.get(i + 2);
            byte alpha = buf.get(i + 3);
            buf.put(i + 2, red);
            buf.put(i + 1, green);
            buf.put(i, blue);
            buf.put(i + 3, alpha);
        }
        try {
            int yspot = imageData.getHeight() - y - 1;
            if (yspot < 0) {
                yspot = 0;
            }
            return new Cursor(imageData.getTexWidth(), imageData.getTexHeight(), x, yspot, 1, buf.asIntBuffer(), null);
        }
        catch (Throwable e) {
            Log.info("Chances are you cursor is too small for this platform");
            throw new LWJGLException(e);
        }
    }

    public Cursor getAnimatedCursor(String ref, int x, int y, int width, int height, int[] cursorDelays) throws IOException, LWJGLException {
        IntBuffer cursorDelaysBuffer = ByteBuffer.allocateDirect(cursorDelays.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < cursorDelays.length; ++i) {
            cursorDelaysBuffer.put(cursorDelays[i]);
        }
        cursorDelaysBuffer.flip();
        TGAImageData imageData = new TGAImageData();
        ByteBuffer buf = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), false, null);
        return new Cursor(width, height, x, y, cursorDelays.length, buf.asIntBuffer(), cursorDelaysBuffer);
    }
}

