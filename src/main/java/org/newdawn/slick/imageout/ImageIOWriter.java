package org.newdawn.slick.imageout;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.imageout.ImageWriter;

public class ImageIOWriter
implements ImageWriter {
    @Override
    public void saveImage(Image image, String format, OutputStream output, boolean hasAlpha) throws IOException {
        ComponentColorModel cm;
        PixelInterleavedSampleModel sampleModel;
        int[] offsets;
        int len = 4 * image.getWidth() * image.getHeight();
        if (!hasAlpha) {
            len = 3 * image.getWidth() * image.getHeight();
        }
        ByteBuffer out = ByteBuffer.allocate(len);
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                Color c = image.getColor(x, y);
                out.put((byte)(c.r * 255.0f));
                out.put((byte)(c.g * 255.0f));
                out.put((byte)(c.b * 255.0f));
                if (!hasAlpha) continue;
                out.put((byte)(c.a * 255.0f));
            }
        }
        DataBufferByte dataBuffer = new DataBufferByte(out.array(), len);
        if (hasAlpha) {
            offsets = new int[]{0, 1, 2, 3};
            sampleModel = new PixelInterleavedSampleModel(0, image.getWidth(), image.getHeight(), 4, 4 * image.getWidth(), offsets);
            cm = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8, 8}, true, false, 3, 0);
        } else {
            offsets = new int[]{0, 1, 2};
            sampleModel = new PixelInterleavedSampleModel(0, image.getWidth(), image.getHeight(), 3, 3 * image.getWidth(), offsets);
            cm = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8, 0}, false, false, 1, 0);
        }
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
        BufferedImage img = new BufferedImage(cm, raster, false, null);
        ImageIO.write((RenderedImage)img, format, output);
    }
}
