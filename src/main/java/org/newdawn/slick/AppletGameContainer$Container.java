package org.newdawn.slick;

import java.applet.Applet;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.util.Log;

public class AppletGameContainer$Container
extends GameContainer {
    public AppletGameContainer$Container(Game game) {
        super(game);
        this.width = AppletGameContainer.this.getWidth();
        this.height = AppletGameContainer.this.getHeight();
    }

    public void initApplet() throws SlickException {
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
        this.game.init(this);
        this.getDelta();
    }

    public boolean isRunning() {
        return this.running;
    }

    public void stopApplet() {
        this.running = false;
    }

    @Override
    public int getScreenHeight() {
        return 0;
    }

    @Override
    public int getScreenWidth() {
        return 0;
    }

    public boolean supportsAlphaInBackBuffer() {
        return AppletGameContainer.this.alphaSupport;
    }

    @Override
    public boolean hasFocus() {
        return true;
    }

    public Applet getApplet() {
        return AppletGameContainer.this;
    }

    @Override
    public void setIcon(String ref) throws SlickException {
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
    public void setIcons(String[] refs) throws SlickException {
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

    @Override
    public void setDefaultMouseCursor() {
    }

    @Override
    public boolean isFullscreen() {
        return Display.isFullscreen();
    }

    @Override
    public void setFullscreen(boolean fullscreen) throws SlickException {
        if (fullscreen == this.isFullscreen()) {
            return;
        }
        try {
            if (fullscreen) {
                int newHeight;
                int newWidth;
                int screenHeight;
                float gameAspectRatio = (float)this.width / (float)this.height;
                int screenWidth = Display.getDisplayMode().getWidth();
                float screenAspectRatio = (float)screenWidth / (float)(screenHeight = Display.getDisplayMode().getHeight());
                if (gameAspectRatio >= screenAspectRatio) {
                    newWidth = screenWidth;
                    newHeight = (int)((float)this.height / ((float)this.width / (float)screenWidth));
                } else {
                    newWidth = (int)((float)this.width / ((float)this.height / (float)screenHeight));
                    newHeight = screenHeight;
                }
                int xoffset = (screenWidth - newWidth) / 2;
                int yoffset = (screenHeight - newHeight) / 2;
                GL11.glViewport(xoffset, yoffset, newWidth, newHeight);
                this.enterOrtho();
                this.getInput().setOffset((float)(-xoffset) * (float)this.width / (float)newWidth, (float)(-yoffset) * (float)this.height / (float)newHeight);
                this.getInput().setScale((float)this.width / (float)newWidth, (float)this.height / (float)newHeight);
                this.width = screenWidth;
                this.height = screenHeight;
                Display.setFullscreen(true);
            } else {
                this.getInput().setOffset(0.0f, 0.0f);
                this.getInput().setScale(1.0f, 1.0f);
                this.width = AppletGameContainer.this.getWidth();
                this.height = AppletGameContainer.this.getHeight();
                GL11.glViewport(0, 0, this.width, this.height);
                this.enterOrtho();
                Display.setFullscreen(false);
            }
        }
        catch (LWJGLException e) {
            Log.error(e);
        }
    }

    public void runloop() throws Exception {
        while (this.running) {
            int delta = this.getDelta();
            this.updateAndRender(delta);
            this.updateFPS();
            Display.update();
        }
        Display.destroy();
    }
}
