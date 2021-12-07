/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;

public class TextureLoader {
    public static Texture getTexture(String format, InputStream in) throws IOException {
        return TextureLoader.getTexture(format, in, false, 9729);
    }

    public static Texture getTexture(String format, InputStream in, boolean flipped) throws IOException {
        return TextureLoader.getTexture(format, in, flipped, 9729);
    }

    public static Texture getTexture(String format, InputStream in, int filter) throws IOException {
        return TextureLoader.getTexture(format, in, false, filter);
    }

    public static Texture getTexture(String format, InputStream in, boolean flipped, int filter) throws IOException {
        return InternalTextureLoader.get().getTexture(in, in.toString() + "." + format, flipped, filter);
    }
}

