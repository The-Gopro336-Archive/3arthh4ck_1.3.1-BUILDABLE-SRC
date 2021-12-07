/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.input.Cursor
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.PixelFormat
 */
package org.newdawn.slick;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;

public class AppletGameContainer
extends Applet {
    protected ContainerPanel canvas;
    protected Container container;
    protected Canvas displayParent;
    protected Thread gameThread;
    protected boolean alphaSupport = true;

    @Override
    public void destroy() {
        if (this.displayParent != null) {
            this.remove(this.displayParent);
        }
        super.destroy();
        Log.info("Clear up");
    }

    private void destroyLWJGL() {
        this.container.stopApplet();
        try {
            this.gameThread.join();
        }
        catch (InterruptedException e) {
            Log.error(e);
        }
    }

    @Override
    public void start() {
    }

    public void startLWJGL() {
        if (this.gameThread != null) {
            return;
        }
        this.gameThread = new Thread(){

            @Override
            public void run() {
                try {
                    AppletGameContainer.this.canvas.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (Display.isCreated()) {
                        Display.destroy();
                    }
                    AppletGameContainer.this.displayParent.setVisible(false);
                    AppletGameContainer.this.add(new ConsolePanel(e));
                    AppletGameContainer.this.validate();
                }
            }
        };
        this.gameThread.start();
    }

    @Override
    public void stop() {
    }

    @Override
    public void init() {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.setIgnoreRepaint(true);
        try {
            Game game = (Game)Class.forName(this.getParameter("game")).newInstance();
            this.container = new Container(game);
            this.canvas = new ContainerPanel(this.container);
            this.displayParent = new Canvas(){

                @Override
                public final void addNotify() {
                    super.addNotify();
                    AppletGameContainer.this.startLWJGL();
                }

                @Override
                public final void removeNotify() {
                    AppletGameContainer.this.destroyLWJGL();
                    super.removeNotify();
                }
            };
            this.displayParent.setSize(this.getWidth(), this.getHeight());
            this.add(this.displayParent);
            this.displayParent.setFocusable(true);
            this.displayParent.requestFocus();
            this.displayParent.setIgnoreRepaint(true);
            this.setVisible(true);
        }
        catch (Exception e) {
            Log.error(e);
            throw new RuntimeException("Unable to create game container");
        }
    }

    public GameContainer getContainer() {
        return this.container;
    }

    public class ConsolePanel
    extends Panel {
        TextArea textArea = new TextArea();

        public ConsolePanel(Exception e) {
            this.setLayout(new BorderLayout());
            this.setBackground(Color.black);
            this.setForeground(Color.white);
            Font consoleFont = new Font("Arial", 1, 14);
            Label slickLabel = new Label("SLICK CONSOLE", 1);
            slickLabel.setFont(consoleFont);
            this.add((Component)slickLabel, "First");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            this.textArea.setText(sw.toString());
            this.textArea.setEditable(false);
            this.add((Component)this.textArea, "Center");
            this.add((Component)new Panel(), "Before");
            this.add((Component)new Panel(), "After");
            Panel bottomPanel = new Panel();
            bottomPanel.setLayout(new GridLayout(0, 1));
            Label infoLabel1 = new Label("An error occured while running the applet.", 1);
            Label infoLabel2 = new Label("Plese contact support to resolve this issue.", 1);
            infoLabel1.setFont(consoleFont);
            infoLabel2.setFont(consoleFont);
            bottomPanel.add(infoLabel1);
            bottomPanel.add(infoLabel2);
            this.add((Component)bottomPanel, "Last");
        }
    }

    public class Container
    extends GameContainer {
        public Container(Game game) {
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
            Mouse.setGrabbed((boolean)grabbed);
        }

        @Override
        public boolean isMouseGrabbed() {
            return Mouse.isGrabbed();
        }

        @Override
        public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
            try {
                Cursor cursor = CursorLoader.get().getCursor(ref, hotSpotX, hotSpotY);
                Mouse.setNativeCursor((Cursor)cursor);
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
                ByteBuffer buffer = BufferUtils.createByteBuffer((int)(temp.getWidth() * temp.getHeight() * 4));
                g.drawImage(image.getFlippedCopy(false, true), 0.0f, 0.0f);
                g.flush();
                g.getArea(0, 0, temp.getWidth(), temp.getHeight(), buffer);
                Cursor cursor = CursorLoader.get().getCursor(buffer, hotSpotX, hotSpotY, temp.getWidth(), temp.getHeight());
                Mouse.setNativeCursor((Cursor)cursor);
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
                Mouse.setNativeCursor((Cursor)cursor);
            }
            catch (Throwable e) {
                Log.error("Failed to load and apply cursor.", e);
                throw new SlickException("Failed to set mouse cursor", e);
            }
        }

        @Override
        public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException {
            try {
                Mouse.setNativeCursor((Cursor)cursor);
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
                    GL11.glViewport((int)xoffset, (int)yoffset, (int)newWidth, (int)newHeight);
                    this.enterOrtho();
                    this.getInput().setOffset((float)(-xoffset) * (float)this.width / (float)newWidth, (float)(-yoffset) * (float)this.height / (float)newHeight);
                    this.getInput().setScale((float)this.width / (float)newWidth, (float)this.height / (float)newHeight);
                    this.width = screenWidth;
                    this.height = screenHeight;
                    Display.setFullscreen((boolean)true);
                } else {
                    this.getInput().setOffset(0.0f, 0.0f);
                    this.getInput().setScale(1.0f, 1.0f);
                    this.width = AppletGameContainer.this.getWidth();
                    this.height = AppletGameContainer.this.getHeight();
                    GL11.glViewport((int)0, (int)0, (int)this.width, (int)this.height);
                    this.enterOrtho();
                    Display.setFullscreen((boolean)false);
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

    public class ContainerPanel {
        private Container container;

        public ContainerPanel(Container container) {
            this.container = container;
        }

        private void createDisplay() throws Exception {
            try {
                Display.create((PixelFormat)new PixelFormat(8, 8, GameContainer.stencil ? 8 : 0));
                AppletGameContainer.this.alphaSupport = true;
            }
            catch (Exception e) {
                AppletGameContainer.this.alphaSupport = false;
                Display.destroy();
                Display.create();
            }
        }

        public void start() throws Exception {
            Display.setParent((Canvas)AppletGameContainer.this.displayParent);
            Display.setVSyncEnabled((boolean)true);
            try {
                this.createDisplay();
            }
            catch (LWJGLException e) {
                e.printStackTrace();
                Thread.sleep(1000L);
                this.createDisplay();
            }
            this.initGL();
            AppletGameContainer.this.displayParent.requestFocus();
            this.container.runloop();
        }

        protected void initGL() {
            try {
                InternalTextureLoader.get().clear();
                SoundStore.get().clear();
                this.container.initApplet();
            }
            catch (Exception e) {
                Log.error(e);
                this.container.stopApplet();
            }
        }
    }
}

