package me.earth.earthhack.impl.util.render.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.render.image.ImageUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.Sys;

public class GifImage
implements Globals {
    private List<BufferedImage> frames = new LinkedList<BufferedImage>();
    private List<DynamicTexture> textures = new LinkedList<DynamicTexture>();
    private int offset;
    private int delay;
    private boolean firstUpdate;
    private long lastUpdate;
    private long timeLeft;

    public GifImage(List<BufferedImage> images, int delay) {
        this.frames.clear();
        for (BufferedImage image : images) {
            this.frames.add(ImageUtil.createFlipped(image));
        }
        this.offset = 0;
        this.delay = delay;
        this.firstUpdate = true;
        for (BufferedImage image : this.frames) {
            try {
                this.textures.add(ImageUtil.cacheBufferedImage(image, "gif"));
            }
            catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        this.reset();
    }

    public GifImage(BufferedImage sheet, int width, int height) {
    }

    public void reset() {
        this.firstUpdate = true;
        this.timeLeft = this.delay;
        this.offset = 0;
    }

    public BufferedImage getBufferedImage() {
        long now = this.getTime();
        long delta = now - this.lastUpdate;
        if (this.firstUpdate) {
            delta = 0L;
            this.firstUpdate = false;
        }
        this.lastUpdate = now;
        this.timeLeft -= delta;
        if (this.timeLeft <= 0L) {
            ++this.offset;
            this.timeLeft = this.delay;
        }
        if (this.offset >= this.frames.size()) {
            this.offset = 0;
        }
        return this.frames.get(this.offset);
    }

    public DynamicTexture getDynamicTexture() {
        long now = this.getTime();
        long delta = now - this.lastUpdate;
        if (this.firstUpdate) {
            delta = 0L;
            this.firstUpdate = false;
        }
        this.lastUpdate = now;
        this.timeLeft -= delta;
        if (this.timeLeft <= 0L) {
            ++this.offset;
            this.timeLeft = this.delay;
        }
        if (this.offset >= this.frames.size()) {
            this.offset = 0;
        }
        return this.textures.get(this.offset);
    }

    private long getTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }
}
