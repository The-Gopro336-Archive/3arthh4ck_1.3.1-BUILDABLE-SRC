/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.render.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.ImageFrame;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.TextureLoader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GifConverter {
    public static Animation readAnimation(InputStream stream) throws IOException {
        ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));
        int lastx = 0;
        int lasty = 0;
        int width = -1;
        int height = -1;
        IIOMetadata metadata = reader.getStreamMetadata();
        Color backgroundColor = null;
        if (metadata != null) {
            IIOMetadataNode colorTable;
            IIOMetadataNode screenDescriptor;
            IIOMetadataNode globalRoot = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
            NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
            if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0 && (screenDescriptor = (IIOMetadataNode)globalScreeDescriptor.item(0)) != null) {
                width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
            }
            if (globalColorTable != null && globalColorTable.getLength() > 0 && (colorTable = (IIOMetadataNode)globalColorTable.item(0)) != null) {
                String bgIndex = colorTable.getAttribute("backgroundColorIndex");
                for (IIOMetadataNode colorEntry = (IIOMetadataNode)colorTable.getFirstChild(); colorEntry != null; colorEntry = (IIOMetadataNode)colorEntry.getNextSibling()) {
                    if (!colorEntry.getAttribute("index").equals(bgIndex)) continue;
                    int red = Integer.parseInt(colorEntry.getAttribute("red"));
                    int green = Integer.parseInt(colorEntry.getAttribute("green"));
                    int blue = Integer.parseInt(colorEntry.getAttribute("blue"));
                    backgroundColor = new Color(red, green, blue);
                    break;
                }
            }
        }
        BufferedImage master = null;
        boolean hasBackround = false;
        int frameIndex = 0;
        while (true) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            }
            catch (IndexOutOfBoundsException io) {
                break;
            }
            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }
            IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
            NodeList children = root.getChildNodes();
            int delay = Integer.valueOf(gce.getAttribute("delayTime"));
            String disposal = gce.getAttribute("disposalMethod");
            if (master == null) {
                master = new BufferedImage(width, height, 2);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());
                hasBackround = image.getWidth() == width && image.getHeight() == height;
                master.createGraphics().drawImage((Image)image, 0, 0, null);
            } else {
                int n;
                boolean bl = false;
                int y = 0;
                for (int nodeIndex = 0; nodeIndex < children.getLength(); ++nodeIndex) {
                    Node nodeItem = children.item(nodeIndex);
                    if (!nodeItem.getNodeName().equals("ImageDescriptor")) continue;
                    NamedNodeMap map = nodeItem.getAttributes();
                    n = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                    y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
                }
                if (disposal.equals("restoreToPrevious")) {
                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; --i) {
                        if (((ImageFrame)frames.get(i)).getDisposal().equals("restoreToPrevious") && frameIndex != 0) continue;
                        from = ((ImageFrame)frames.get(i)).getImage();
                        break;
                    }
                    ColorModel model = from.getColorModel();
                    boolean alpha = from.isAlphaPremultiplied();
                    WritableRaster raster = from.copyData(null);
                    master = new BufferedImage(model, raster, alpha, null);
                } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null && (!hasBackround || frameIndex > 1)) {
                    master.createGraphics().fillRect(lastx, lasty, ((ImageFrame)frames.get(frameIndex - 1)).getWidth(), ((ImageFrame)frames.get(frameIndex - 1)).getHeight());
                }
                master.createGraphics().drawImage((Image)image, n, y, null);
                lastx = n;
                lasty = y;
            }
            ColorModel model = master.getColorModel();
            int alpha = master.isAlphaPremultiplied() ? 1 : 0;
            WritableRaster raster = master.copyData(null);
            BufferedImage bufferedImage = new BufferedImage(model, raster, alpha != 0, null);
            frames.add(new ImageFrame(bufferedImage, delay, disposal, image.getWidth(), image.getHeight()));
            master.flush();
            ++frameIndex;
        }
        reader.dispose();
        ImageFrame[] framez = frames.toArray(new ImageFrame[frames.size()]);
        int totalWidth = 0;
        int maxHeight = framez[0].getHeight();
        for (ImageFrame frame : framez) {
            totalWidth += frame.getWidth();
            if (frame.getHeight() <= maxHeight) continue;
            maxHeight = frame.getHeight();
        }
        BufferedImage finalImage = new BufferedImage(totalWidth, maxHeight, 1);
        Graphics graphics = finalImage.getGraphics();
        int offset = 0;
        int delay = 0;
        for (ImageFrame frame : framez) {
            graphics.drawImage(frame.getImage(), offset, 0, null);
            offset += frame.getWidth();
            delay += frame.getDelay() * 10;
        }
        graphics.dispose();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)finalImage, "gif", byteArrayOutputStream);
        ByteArrayInputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        org.newdawn.slick.Image image1 = new org.newdawn.slick.Image(TextureLoader.getTexture("GIF", is));
        return new Animation(new SpriteSheet(image1, width, height), delay /= framez.length);
    }

    public static GifImage readGifImage(InputStream stream) throws IOException {
        ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));
        int lastx = 0;
        int lasty = 0;
        int width = -1;
        int height = -1;
        IIOMetadata metadata = reader.getStreamMetadata();
        Color backgroundColor = null;
        if (metadata != null) {
            IIOMetadataNode colorTable;
            IIOMetadataNode screenDescriptor;
            IIOMetadataNode globalRoot = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
            NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
            if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0 && (screenDescriptor = (IIOMetadataNode)globalScreeDescriptor.item(0)) != null) {
                width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
            }
            if (globalColorTable != null && globalColorTable.getLength() > 0 && (colorTable = (IIOMetadataNode)globalColorTable.item(0)) != null) {
                String bgIndex = colorTable.getAttribute("backgroundColorIndex");
                for (IIOMetadataNode colorEntry = (IIOMetadataNode)colorTable.getFirstChild(); colorEntry != null; colorEntry = (IIOMetadataNode)colorEntry.getNextSibling()) {
                    if (!colorEntry.getAttribute("index").equals(bgIndex)) continue;
                    int red = Integer.parseInt(colorEntry.getAttribute("red"));
                    int green = Integer.parseInt(colorEntry.getAttribute("green"));
                    int blue = Integer.parseInt(colorEntry.getAttribute("blue"));
                    backgroundColor = new Color(red, green, blue);
                    break;
                }
            }
        }
        BufferedImage master = null;
        boolean hasBackround = false;
        int frameIndex = 0;
        while (true) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            }
            catch (IndexOutOfBoundsException io) {
                break;
            }
            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }
            IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
            NodeList children = root.getChildNodes();
            int delay = Integer.valueOf(gce.getAttribute("delayTime"));
            String disposal = gce.getAttribute("disposalMethod");
            if (master == null) {
                master = new BufferedImage(width, height, 2);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());
                hasBackround = image.getWidth() == width && image.getHeight() == height;
                master.createGraphics().drawImage((Image)image, 0, 0, null);
            } else {
                int n;
                int x = 0;
                boolean bl = false;
                for (int nodeIndex = 0; nodeIndex < children.getLength(); ++nodeIndex) {
                    Node nodeItem = children.item(nodeIndex);
                    if (!nodeItem.getNodeName().equals("ImageDescriptor")) continue;
                    NamedNodeMap map = nodeItem.getAttributes();
                    x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                    n = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
                }
                if (disposal.equals("restoreToPrevious")) {
                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; --i) {
                        if (((ImageFrame)frames.get(i)).getDisposal().equals("restoreToPrevious") && frameIndex != 0) continue;
                        from = ((ImageFrame)frames.get(i)).getImage();
                        break;
                    }
                    ColorModel model = from.getColorModel();
                    boolean alpha = from.isAlphaPremultiplied();
                    WritableRaster raster = from.copyData(null);
                    master = new BufferedImage(model, raster, alpha, null);
                } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null && (!hasBackround || frameIndex > 1)) {
                    master.createGraphics().fillRect(lastx, lasty, ((ImageFrame)frames.get(frameIndex - 1)).getWidth(), ((ImageFrame)frames.get(frameIndex - 1)).getHeight());
                }
                master.createGraphics().drawImage((Image)image, x, n, null);
                lastx = x;
                lasty = n;
            }
            ColorModel colorModel = master.getColorModel();
            int alpha = master.isAlphaPremultiplied() ? 1 : 0;
            WritableRaster raster = master.copyData(null);
            BufferedImage copy = new BufferedImage(colorModel, raster, alpha != 0, null);
            frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));
            master.flush();
            ++frameIndex;
        }
        reader.dispose();
        ImageFrame[] framez = frames.toArray(new ImageFrame[frames.size()]);
        int totalWidth = 0;
        int maxHeight = framez[0].getHeight();
        for (ImageFrame frame : framez) {
            totalWidth += frame.getWidth();
            if (frame.getHeight() <= maxHeight) continue;
            maxHeight = frame.getHeight();
        }
        BufferedImage finalImage = new BufferedImage(totalWidth, maxHeight, 1);
        Graphics graphics = finalImage.getGraphics();
        boolean offset = false;
        int delay = 0;
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        for (ImageFrame frame : framez) {
            delay += frame.getDelay() * 10;
            images.add(frame.getImage());
        }
        graphics.dispose();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)finalImage, "gif", byteArrayOutputStream);
        ByteArrayInputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return new GifImage(images, delay /= framez.length);
    }
}

