/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.Pbuffer
 *  org.lwjgl.opengl.PixelFormat
 */
package org.newdawn.slick.opengl.pbuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.Log;

public class PBufferUniqueGraphics
extends Graphics {
    private Pbuffer pbuffer;
    private Image image;

    public PBufferUniqueGraphics(Image image) throws SlickException {
        super(image.getTexture().getTextureWidth(), image.getTexture().getTextureHeight());
        this.image = image;
        Log.debug("Creating pbuffer(unique) " + image.getWidth() + "x" + image.getHeight());
        if ((Pbuffer.getCapabilities() & 1) == 0) {
            throw new SlickException("Your OpenGL card does not support PBuffers and hence can't handle the dynamic images required for this application.");
        }
        this.init();
    }

    private void init() throws SlickException {
        try {
            Texture tex = InternalTextureLoader.get().createTexture(this.image.getWidth(), this.image.getHeight(), this.image.getFilter());
            this.pbuffer = new Pbuffer(this.screenWidth, this.screenHeight, new PixelFormat(8, 0, 0), null, null);
            this.pbuffer.makeCurrent();
            this.initGL();
            this.image.draw(0.0f, 0.0f);
            GL11.glBindTexture((int)3553, (int)tex.getTextureID());
            GL11.glCopyTexImage2D((int)3553, (int)0, (int)6408, (int)0, (int)0, (int)tex.getTextureWidth(), (int)tex.getTextureHeight(), (int)0);
            this.image.setTexture(tex);
            Display.makeCurrent();
        }
        catch (Exception e) {
            Log.error(e);
            throw new SlickException("Failed to create PBuffer for dynamic image. OpenGL driver failure?");
        }
    }

    @Override
    protected void disable() {
        GL11.glBindTexture((int)3553, (int)this.image.getTexture().getTextureID());
        GL11.glCopyTexImage2D((int)3553, (int)0, (int)6408, (int)0, (int)0, (int)this.image.getTexture().getTextureWidth(), (int)this.image.getTexture().getTextureHeight(), (int)0);
        try {
            Display.makeCurrent();
        }
        catch (LWJGLException e) {
            Log.error(e);
        }
        SlickCallable.leaveSafeBlock();
    }

    @Override
    protected void enable() {
        SlickCallable.enterSafeBlock();
        try {
            if (this.pbuffer.isBufferLost()) {
                this.pbuffer.destroy();
                this.init();
            }
            this.pbuffer.makeCurrent();
        }
        catch (Exception e) {
            Log.error("Failed to recreate the PBuffer");
            Log.error(e);
            throw new RuntimeException(e);
        }
        TextureImpl.unbind();
        this.initGL();
    }

    protected void initGL() {
        GL11.glEnable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)2929);
        GL11.glDisable((int)2896);
        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glClearDepth((double)1.0);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glViewport((int)0, (int)0, (int)this.screenWidth, (int)this.screenHeight);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        this.enterOrtho();
    }

    protected void enterOrtho() {
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)this.screenWidth, (double)0.0, (double)this.screenHeight, (double)1.0, (double)-1.0);
        GL11.glMatrixMode((int)5888);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.pbuffer.destroy();
    }

    @Override
    public void flush() {
        super.flush();
        this.image.flushPixelData();
    }
}

