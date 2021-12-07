package org.newdawn.slick;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.TGAImageData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class AppGameContainer
extends GameContainer {
    protected DisplayMode originalDisplayMode = Display.getDisplayMode();
    protected DisplayMode targetDisplayMode;
    protected boolean updateOnlyOnVisible = true;
    protected boolean alphaSupport = false;

    public AppGameContainer(Game game) throws SlickException {
        this(game, 640, 480, false);
    }

    public AppGameContainer(Game game, int width, int height, boolean fullscreen) throws SlickException {
        super(game);
        this.setDisplayMode(width, height, fullscreen);
    }

    public boolean supportsAlphaInBackBuffer() {
        return this.alphaSupport;
    }

    public void setTitle(String title) {
        Display.setTitle(title);
    }

    public void setDisplayMode(int width, int height, boolean fullscreen) throws SlickException {
        if (this.width == width && this.height == height && this.isFullscreen() == fullscreen) {
            return;
        }
        try {
            this.targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < modes.length; ++i) {
                    DisplayMode current = modes[i];
                    if (current.getWidth() != width || current.getHeight() != height) continue;
                    if (!(this.targetDisplayMode != null && current.getFrequency() < freq || this.targetDisplayMode != null && current.getBitsPerPixel() <= this.targetDisplayMode.getBitsPerPixel())) {
                        this.targetDisplayMode = current;
                        freq = this.targetDisplayMode.getFrequency();
                    }
                    if (current.getBitsPerPixel() != this.originalDisplayMode.getBitsPerPixel() || current.getFrequency() != this.originalDisplayMode.getFrequency()) continue;
                    this.targetDisplayMode = current;
                    break;
                }
            } else {
                this.targetDisplayMode = new DisplayMode(width, height);
            }
            if (this.targetDisplayMode == null) {
                throw new SlickException("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
            }
            this.width = width;
            this.height = height;
            Display.setDisplayMode(this.targetDisplayMode);
            Display.setFullscreen(fullscreen);
            if (Display.isCreated()) {
                this.initGL();
                this.enterOrtho();
            }
            if (this.targetDisplayMode.getBitsPerPixel() == 16) {
                InternalTextureLoader.get().set16BitMode();
            }
        }
        catch (LWJGLException e) {
            throw new SlickException("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen, e);
        }
        this.getDelta();
    }

    @Override
    public boolean isFullscreen() {
        return Display.isFullscreen();
    }

    @Override
    public void setFullscreen(boolean fullscreen) throws SlickException {
        if (this.isFullscreen() == fullscreen) {
            return;
        }
        if (!fullscreen) {
            try {
                Display.setFullscreen(fullscreen);
            }
            catch (LWJGLException e) {
                throw new SlickException("Unable to set fullscreen=" + fullscreen, e);
            }
        } else {
            this.setDisplayMode(this.width, this.height, fullscreen);
        }
        this.getDelta();
    }

    @Override
    public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
        try {
            Cursor cursor = CursorLoader.get().getCursor(ref, hotSpotX, hotSpotY);
            Mouse.setNativeCursor(cursor);
        }
        catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
            throw new SlickException("Failed to set mouse cursor", e);
        }
    }

    @Override
    public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException {
        try {
            Cursor cursor = CursorLoader.get().getCursor(data, hotSpotX, hotSpotY);
            Mouse.setNativeCursor(cursor);
        }
        catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
            throw new SlickException("Failed to set mouse cursor", e);
        }
    }

    @Override
    public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException {
        try {
            Mouse.setNativeCursor(cursor);
        }
        catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
            throw new SlickException("Failed to set mouse cursor", e);
        }
    }

    private int get2Fold(int fold) {
        int ret;
        for (ret = 2; ret < fold; ret *= 2) {
        }
        return ret;
    }

    @Override
    public void setMouseCursor(Image image, int hotSpotX, int hotSpotY) throws SlickException {
        try {
            Image temp = new Image(this.get2Fold(image.getWidth()), this.get2Fold(image.getHeight()));
            Graphics g = temp.getGraphics();
            ByteBuffer buffer = BufferUtils.createByteBuffer(temp.getWidth() * temp.getHeight() * 4);
            g.drawImage(image.getFlippedCopy(false, true), 0.0f, 0.0f);
            g.flush();
            g.getArea(0, 0, temp.getWidth(), temp.getHeight(), buffer);
            Cursor cursor = CursorLoader.get().getCursor(buffer, hotSpotX, hotSpotY, temp.getWidth(), temp.getHeight());
            Mouse.setNativeCursor(cursor);
        }
        catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
            throw new SlickException("Failed to set mouse cursor", e);
        }
    }

    @Override
    public void reinit() throws SlickException {
        InternalTextureLoader.get().clear();
        SoundStore.get().clear();
        this.initSystem();
        this.enterOrtho();
        try {
            this.game.init(this);
        }
        catch (SlickException e) {
            Log.error(e);
            this.running = false;
        }
    }

    private void tryCreateDisplay(PixelFormat format) throws LWJGLException {
        if (SHARED_DRAWABLE == null) {
            Display.create(format);
        } else {
            Display.create(format, SHARED_DRAWABLE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() throws SlickException {
        try {
            this.setup();
            this.getDelta();
            while (this.running()) {
                this.gameLoop();
            }
        }
        finally {
            this.destroy();
        }
        if (this.forceExit) {
            System.exit(0);
        }
    }

    protected void setup() throws SlickException {
        if (this.targetDisplayMode == null) {
            this.setDisplayMode(640, 480, false);
        }
        Display.setTitle(this.game.getTitle());
        Log.info("LWJGL Version: " + Sys.getVersion());
        Log.info("OriginalDisplayMode: " + this.originalDisplayMode);
        Log.info("TargetDisplayMode: " + this.targetDisplayMode);
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    PixelFormat format = new PixelFormat(8, 8, GameContainer.stencil ? 8 : 0, AppGameContainer.this.samples);
                    AppGameContainer.this.tryCreateDisplay(format);
                    AppGameContainer.this.supportsMultiSample = true;
                }
                catch (Exception e) {
                    Display.destroy();
                    try {
                        PixelFormat format = new PixelFormat(8, 8, GameContainer.stencil ? 8 : 0);
                        AppGameContainer.this.tryCreateDisplay(format);
                        AppGameContainer.this.alphaSupport = false;
                    }
                    catch (Exception e2) {
                        Display.destroy();
                        try {
                            AppGameContainer.this.tryCreateDisplay(new PixelFormat());
                        }
                        catch (Exception e3) {
                            Log.error(e3);
                        }
                    }
                }
                return null;
            }
        });
        if (!Display.isCreated()) {
            throw new SlickException("Failed to initialise the LWJGL display");
        }
        this.initSystem();
        this.enterOrtho();
        try {
            this.getInput().initControllers();
        }
        catch (SlickException e) {
            Log.info("Controllers not available");
        }
        catch (Throwable e) {
            Log.info("Controllers not available");
        }
        try {
            this.game.init(this);
        }
        catch (SlickException e) {
            Log.error(e);
            this.running = false;
        }
    }

    protected void gameLoop() throws SlickException {
        int delta = this.getDelta();
        if (!Display.isVisible() && this.updateOnlyOnVisible) {
            try {
                Thread.sleep(100L);
            }
            catch (Exception e) {}
        } else {
            try {
                this.updateAndRender(delta);
            }
            catch (SlickException e) {
                Log.error(e);
                this.running = false;
                return;
            }
        }
        this.updateFPS();
        Display.update();
        if (Display.isCloseRequested() && this.game.closeRequested()) {
            this.running = false;
        }
    }

    @Override
    public void setUpdateOnlyWhenVisible(boolean updateOnlyWhenVisible) {
        this.updateOnlyOnVisible = updateOnlyWhenVisible;
    }

    @Override
    public boolean isUpdatingOnlyWhenVisible() {
        return this.updateOnlyOnVisible;
    }

    @Override
    public void setIcon(String ref) throws SlickException {
        this.setIcons(new String[]{ref});
    }

    @Override
    public void setMouseGrabbed(boolean grabbed) {
        Mouse.setGrabbed(grabbed);
    }

    @Override
    public boolean isMouseGrabbed() {
        return Mouse.isGrabbed();
    }

    @Override
    public boolean hasFocus() {
        return Display.isActive();
    }

    @Override
    public int getScreenHeight() {
        return this.originalDisplayMode.getHeight();
    }

    @Override
    public int getScreenWidth() {
        return this.originalDisplayMode.getWidth();
    }

    public void destroy() {
        Display.destroy();
        AL.destroy();
    }

    @Override
    public void setIcons(String[] refs) throws SlickException {
        ByteBuffer[] bufs = new ByteBuffer[refs.length];
        for (int i = 0; i < refs.length; ++i) {
            LoadableImageData data;
            boolean flip = true;
            if (refs[i].endsWith(".tga")) {
                data = new TGAImageData();
            } else {
                flip = false;
                data = new ImageIOImageData();
            }
            try {
                bufs[i] = data.loadImage(ResourceLoader.getResourceAsStream(refs[i]), flip, false, null);
                continue;
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Failed to set the icon");
            }
        }
        Display.setIcon(bufs);
    }

    @Override
    public void setDefaultMouseCursor() {
        try {
            Mouse.setNativeCursor(null);
        }
        catch (LWJGLException e) {
            Log.error("Failed to reset mouse cursor", e);
        }
    }

    static {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    Display.getDisplayMode();
                }
                catch (Exception e) {
                    Log.error(e);
                }
                return null;
            }
        });
    }

    private class NullOutputStream
    extends OutputStream {
        private NullOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
        }
    }
}
