/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.EXTSecondaryColor
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLContext
 */
package org.newdawn.slick.opengl.renderer;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.EXTSecondaryColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.renderer.SGL;

public class ImmediateModeOGLRenderer
implements SGL {
    private int width;
    private int height;
    private float[] current = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    protected float alphaScale = 1.0f;

    @Override
    public void initDisplay(int width, int height) {
        this.width = width;
        this.height = height;
        String extensions = GL11.glGetString((int)7939);
        GL11.glEnable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)2929);
        GL11.glDisable((int)2896);
        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glClearDepth((double)1.0);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glViewport((int)0, (int)0, (int)width, (int)height);
        GL11.glMatrixMode((int)5888);
    }

    @Override
    public void enterOrtho(int xsize, int ysize) {
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)this.width, (double)this.height, (double)0.0, (double)1.0, (double)-1.0);
        GL11.glMatrixMode((int)5888);
        GL11.glTranslatef((float)((this.width - xsize) / 2), (float)((this.height - ysize) / 2), (float)0.0f);
    }

    @Override
    public void glBegin(int geomType) {
        GL11.glBegin((int)geomType);
    }

    @Override
    public void glBindTexture(int target, int id) {
        GL11.glBindTexture((int)target, (int)id);
    }

    @Override
    public void glBlendFunc(int src, int dest) {
        GL11.glBlendFunc((int)src, (int)dest);
    }

    @Override
    public void glCallList(int id) {
        GL11.glCallList((int)id);
    }

    @Override
    public void glClear(int value) {
        GL11.glClear((int)value);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        GL11.glClearColor((float)red, (float)green, (float)blue, (float)alpha);
    }

    @Override
    public void glClipPlane(int plane, DoubleBuffer buffer) {
        GL11.glClipPlane((int)plane, (DoubleBuffer)buffer);
    }

    @Override
    public void glColor4f(float r, float g, float b, float a) {
        this.current[0] = r;
        this.current[1] = g;
        this.current[2] = b;
        this.current[3] = a *= this.alphaScale;
        GL11.glColor4f((float)r, (float)g, (float)b, (float)a);
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) {
        GL11.glCopyTexImage2D((int)target, (int)level, (int)internalFormat, (int)x, (int)y, (int)width, (int)height, (int)border);
    }

    @Override
    public void glDeleteTextures(IntBuffer buffer) {
        GL11.glDeleteTextures((IntBuffer)buffer);
    }

    @Override
    public void glDisable(int item) {
        GL11.glDisable((int)item);
    }

    @Override
    public void glEnable(int item) {
        GL11.glEnable((int)item);
    }

    @Override
    public void glEnd() {
        GL11.glEnd();
    }

    @Override
    public void glEndList() {
        GL11.glEndList();
    }

    @Override
    public int glGenLists(int count) {
        return GL11.glGenLists((int)count);
    }

    @Override
    public void glGetFloat(int id, FloatBuffer ret) {
        GL11.glGetFloat((int)id, (FloatBuffer)ret);
    }

    @Override
    public void glGetInteger(int id, IntBuffer ret) {
        GL11.glGetInteger((int)id, (IntBuffer)ret);
    }

    @Override
    public void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) {
        GL11.glGetTexImage((int)target, (int)level, (int)format, (int)type, (ByteBuffer)pixels);
    }

    @Override
    public void glLineWidth(float width) {
        GL11.glLineWidth((float)width);
    }

    @Override
    public void glLoadIdentity() {
        GL11.glLoadIdentity();
    }

    @Override
    public void glNewList(int id, int option) {
        GL11.glNewList((int)id, (int)option);
    }

    @Override
    public void glPointSize(float size) {
        GL11.glPointSize((float)size);
    }

    @Override
    public void glPopMatrix() {
        GL11.glPopMatrix();
    }

    @Override
    public void glPushMatrix() {
        GL11.glPushMatrix();
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (ByteBuffer)pixels);
    }

    @Override
    public void glRotatef(float angle, float x, float y, float z) {
        GL11.glRotatef((float)angle, (float)x, (float)y, (float)z);
    }

    @Override
    public void glScalef(float x, float y, float z) {
        GL11.glScalef((float)x, (float)y, (float)z);
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        GL11.glScissor((int)x, (int)y, (int)width, (int)height);
    }

    @Override
    public void glTexCoord2f(float u, float v) {
        GL11.glTexCoord2f((float)u, (float)v);
    }

    @Override
    public void glTexEnvi(int target, int mode, int value) {
        GL11.glTexEnvi((int)target, (int)mode, (int)value);
    }

    @Override
    public void glTranslatef(float x, float y, float z) {
        GL11.glTranslatef((float)x, (float)y, (float)z);
    }

    @Override
    public void glVertex2f(float x, float y) {
        GL11.glVertex2f((float)x, (float)y);
    }

    @Override
    public void glVertex3f(float x, float y, float z) {
        GL11.glVertex3f((float)x, (float)y, (float)z);
    }

    @Override
    public void flush() {
    }

    @Override
    public void glTexParameteri(int target, int param, int value) {
        GL11.glTexParameteri((int)target, (int)param, (int)value);
    }

    @Override
    public float[] getCurrentColor() {
        return this.current;
    }

    @Override
    public void glDeleteLists(int list, int count) {
        GL11.glDeleteLists((int)list, (int)count);
    }

    @Override
    public void glClearDepth(float value) {
        GL11.glClearDepth((double)value);
    }

    @Override
    public void glDepthFunc(int func) {
        GL11.glDepthFunc((int)func);
    }

    @Override
    public void glDepthMask(boolean mask) {
        GL11.glDepthMask((boolean)mask);
    }

    @Override
    public void setGlobalAlphaScale(float alphaScale) {
        this.alphaScale = alphaScale;
    }

    @Override
    public void glLoadMatrix(FloatBuffer buffer) {
        GL11.glLoadMatrix((FloatBuffer)buffer);
    }

    @Override
    public void glGenTextures(IntBuffer ids) {
        GL11.glGenTextures((IntBuffer)ids);
    }

    @Override
    public void glGetError() {
        GL11.glGetError();
    }

    @Override
    public void glTexImage2D(int target, int i, int dstPixelFormat, int width, int height, int j, int srcPixelFormat, int glUnsignedByte, ByteBuffer textureBuffer) {
        GL11.glTexImage2D((int)target, (int)i, (int)dstPixelFormat, (int)width, (int)height, (int)j, (int)srcPixelFormat, (int)glUnsignedByte, (ByteBuffer)textureBuffer);
    }

    @Override
    public void glTexSubImage2D(int glTexture2d, int i, int pageX, int pageY, int width, int height, int glBgra, int glUnsignedByte, ByteBuffer scratchByteBuffer) {
        GL11.glTexSubImage2D((int)glTexture2d, (int)i, (int)pageX, (int)pageY, (int)width, (int)height, (int)glBgra, (int)glUnsignedByte, (ByteBuffer)scratchByteBuffer);
    }

    @Override
    public boolean canTextureMirrorClamp() {
        return GLContext.getCapabilities().GL_EXT_texture_mirror_clamp;
    }

    @Override
    public boolean canSecondaryColor() {
        return GLContext.getCapabilities().GL_EXT_secondary_color;
    }

    @Override
    public void glSecondaryColor3ubEXT(byte b, byte c, byte d) {
        EXTSecondaryColor.glSecondaryColor3ubEXT((byte)b, (byte)c, (byte)d);
    }
}

