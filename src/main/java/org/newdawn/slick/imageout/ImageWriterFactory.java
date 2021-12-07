/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.imageout;

import java.util.HashMap;
import javax.imageio.ImageIO;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageIOWriter;
import org.newdawn.slick.imageout.ImageWriter;
import org.newdawn.slick.imageout.TGAWriter;

public class ImageWriterFactory {
    private static HashMap writers = new HashMap();

    public static void registerWriter(String format, ImageWriter writer) {
        writers.put(format, writer);
    }

    public static String[] getSupportedFormats() {
        return writers.keySet().toArray(new String[0]);
    }

    public static ImageWriter getWriterForFormat(String format) throws SlickException {
        ImageWriter writer = (ImageWriter)writers.get(format);
        if (writer != null) {
            return writer;
        }
        writer = (ImageWriter)writers.get(format.toLowerCase());
        if (writer != null) {
            return writer;
        }
        writer = (ImageWriter)writers.get(format.toUpperCase());
        if (writer != null) {
            return writer;
        }
        throw new SlickException("No image writer available for: " + format);
    }

    static {
        String[] formats = ImageIO.getWriterFormatNames();
        ImageIOWriter writer = new ImageIOWriter();
        for (int i = 0; i < formats.length; ++i) {
            ImageWriterFactory.registerWriter(formats[i], writer);
        }
        TGAWriter tga = new TGAWriter();
        ImageWriterFactory.registerWriter("tga", tga);
    }
}

