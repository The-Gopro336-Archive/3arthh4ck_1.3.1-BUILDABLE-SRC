/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLContext
 */
package org.newdawn.slick.opengl.pbuffer;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.Log;

public class FBOGraphics
extends Graphics {
    private Image image;
    private int FBO;
    private boolean valid = true;

    public FBOGraphics(Image image) throws SlickException {
        super(image.getTexture().getTextureWidth(), image.getTexture().getTextureHeight());
        this.image = image;
        Log.debug("Creating FBO " + image.getWidth() + "x" + image.getHeight());
        boolean FBOEnabled = GLContext.getCapabilities().GL_EXT_framebuffer_object;
        if (!FBOEnabled) {
            throw new SlickException("Your OpenGL card does not support FBO and hence can't handle the dynamic images required for this application.");
        }
        this.init();
    }

    private void completeCheck() throws SlickException {
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        switch (framebuffer) {
            case 36053: {
                break;
            }
            case 36054: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
            }
            case 36055: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
            }
            case 36057: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
            }
            case 36059: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
            }
            case 36058: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
            }
            case 36060: {
                throw new SlickException("FrameBuffer: " + this.FBO + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
            }
            default: {
                throw new SlickException("Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer);
            }
        }
    }

    private void init() throws SlickException {
        IntBuffer buffer = BufferUtils.createIntBuffer((int)1);
        EXTFramebufferObject.glGenFramebuffersEXT((IntBuffer)buffer);
        this.FBO = buffer.get();
        try {
            Texture tex = InternalTextureLoader.get().createTexture(this.image.getWidth(), this.image.getHeight(), this.image.getFilter());
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)this.FBO);
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36064, (int)3553, (int)tex.getTextureID(), (int)0);
            this.completeCheck();
            this.unbind();
            this.clear();
            this.flush();
            this.drawImage(this.image, 0.0f, 0.0f);
            this.image.setTexture(tex);
        }
        catch (Exception e) {
            throw new SlickException("Failed to create new texture for FBO");
        }
    }

    private void bind() {
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)this.FBO);
        GL11.glReadBuffer((int)36064);
    }

    private void unbind() {
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)0);
        GL11.glReadBuffer((int)1029);
    }

    @Override
    protected void disable() {
        GL.flush();
        this.unbind();
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        GL11.glMatrixMode((int)5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode((int)5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode((int)5888);
        SlickCallable.leaveSafeBlock();
    }

    @Override
    protected void enable() {
        if (!this.valid) {
            throw new RuntimeException("Attempt to use a destroy()ed offscreen graphics context.");
        }
        SlickCallable.enterSafeBlock();
        GL11.glPushAttrib((int)1048575);
        GL11.glPushClientAttrib((int)-1);
        GL11.glMatrixMode((int)5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5888);
        GL11.glPushMatrix();
        this.bind();
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
        IntBuffer buffer = BufferUtils.createIntBuffer((int)1);
        buffer.put(this.FBO);
        buffer.flip();
        EXTFramebufferObject.glDeleteFramebuffersEXT((IntBuffer)buffer);
        this.valid = false;
    }

    @Override
    public void flush() {
        super.flush();
        this.image.flushPixelData();
    }
}

