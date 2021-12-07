package me.earth.earthhack.impl.util.render;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import me.earth.earthhack.impl.util.helpers.SafeFinishable;
import net.minecraft.client.renderer.texture.TextureUtil;

public class ScreenShotRunnable
extends SafeFinishable {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private final AtomicReference<String> finishedString;
    private final AtomicReference<File> fileReference;
    private final String screenShotName;
    private final File dir;
    private final int width;
    private final int height;
    private final int[] pixels;

    public ScreenShotRunnable(AtomicReference<String> finishedString, AtomicReference<File> fileReference, AtomicBoolean finished, int width, int height, int[] pixels, File dir, @Nullable String screenshotName) {
        super(finished);
        this.fileReference = fileReference;
        this.finishedString = finishedString;
        this.width = width;
        this.height = height;
        this.pixels = pixels;
        this.dir = dir;
        this.screenShotName = screenshotName;
    }

    @Override
    public void runSafely() throws IOException {
        TextureUtil.processPixelValues((int[])this.pixels, (int)this.width, (int)this.height);
        BufferedImage bufferedimage = new BufferedImage(this.width, this.height, 1);
        bufferedimage.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
        File file = ScreenShotRunnable.makeScreenshotFile(this.dir, this.screenShotName);
        ImageIO.write((RenderedImage)bufferedimage, "png", file);
        this.finishedString.set("Screenshot: " + file.getName());
        this.fileReference.set(file);
    }

    @Override
    public void handle(Throwable t) {
        this.finishedString.set("Screenshot Error: " + t.getMessage());
        super.handle(t);
    }

    private static File makeScreenshotFile(File gameDirectory, @Nullable String screenshotName) throws IOException {
        File directory = new File(gameDirectory, "screenshots");
        directory.mkdir();
        File file = screenshotName == null ? ScreenShotRunnable.getTimestampedPNGFileForDirectory(directory) : new File(directory, screenshotName);
        file = file.getCanonicalFile();
        return file;
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
        String s = DATE_FORMAT.format(new Date());
        int i = 1;
        File file;
        while ((file = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file;
    }
}
