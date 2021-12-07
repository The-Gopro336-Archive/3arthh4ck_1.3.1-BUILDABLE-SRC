package me.earth.earthhack.impl.util.render.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageWriterFactory;
import org.newdawn.slick.opengl.TextureLoader;

public class ImageUtil
implements Globals {
    public static BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1.0, -1.0));
        at.concatenate(AffineTransform.getTranslateInstance(0.0, -image.getHeight()));
        return ImageUtil.createTransformed(image, at);
    }

    public static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage((Image)image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static DynamicTexture cacheBufferedImage(BufferedImage image, String format) throws NoSuchAlgorithmException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)image, format, outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);
        DynamicTexture texture = new DynamicTexture(image);
        ResourceLocation location = mc.getTextureManager().getDynamicTextureLocation(name, texture);
        mc.getTextureManager().loadTexture(location, texture);
        return texture;
    }

    public static void bindImage(BufferedImage bufferedImage) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String format = ImageUtil.getImageFormat(bufferedImage);
        if (format == null) {
            throw new IOException();
        }
        ImageIO.write((RenderedImage)bufferedImage, format, outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        ResourceLocation location = mc.getTextureManager().getDynamicTextureLocation(name, texture);
        mc.getTextureManager().bindTexture(location);
    }

    public static void bindImage(BufferedImage bufferedImage, String format) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (format == null) {
            throw new IOException();
        }
        ImageIO.write((RenderedImage)bufferedImage, format, outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        ResourceLocation location = mc.getTextureManager().getDynamicTextureLocation(name, texture);
        mc.getTextureManager().bindTexture(location);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getImageFormat(BufferedImage image) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(image);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
        if (!iter.hasNext()) {
            return null;
        }
        ImageReader reader = iter.next();
        ImageReadParam param = reader.getDefaultReadParam();
        reader.setInput(stream, true, true);
        try {
            BufferedImage bi = reader.read(0, param);
            String string = reader.getFormatName();
            return string;
        }
        finally {
            reader.dispose();
            stream.close();
        }
    }

    public static org.newdawn.slick.Image bufferedImageToSlickImage(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)image, format, baos);
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        return new org.newdawn.slick.Image(TextureLoader.getTexture(format, is));
    }

    public static BufferedImage slickImageToBufferedImage(org.newdawn.slick.Image image, String format) throws SlickException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriterFactory.getWriterForFormat(format).saveImage(image, format, baos, true);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return ImageIO.read(bais);
    }

    public static BufferedImage getCurrentAnimationFrame(Animation animation) throws SlickException, IOException {
        return ImageUtil.slickImageToBufferedImage(animation.getCurrentFrame(), "GIF");
    }
}
