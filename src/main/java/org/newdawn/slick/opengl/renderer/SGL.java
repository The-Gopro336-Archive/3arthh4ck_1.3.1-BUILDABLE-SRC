/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.opengl.renderer;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface SGL {
    public static final int GL_TEXTURE_2D = 3553;
    public static final int GL_RGBA = 6408;
    public static final int GL_RGB = 6407;
    public static final int GL_UNSIGNED_BYTE = 5121;
    public static final int GL_LINEAR = 9729;
    public static final int GL_NEAREST = 9728;
    public static final int GL_TEXTURE_MIN_FILTER = 10241;
    public static final int GL_TEXTURE_MAG_FILTER = 10240;
    public static final int GL_POINT_SMOOTH = 2832;
    public static final int GL_POLYGON_SMOOTH = 2881;
    public static final int GL_LINE_SMOOTH = 2848;
    public static final int GL_SCISSOR_TEST = 3089;
    public static final int GL_MODULATE = 8448;
    public static final int GL_TEXTURE_ENV = 8960;
    public static final int GL_TEXTURE_ENV_MODE = 8704;
    public static final int GL_QUADS = 7;
    public static final int GL_POINTS = 0;
    public static final int GL_LINES = 1;
    public static final int GL_LINE_STRIP = 3;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_TRIANGLE_FAN = 6;
    public static final int GL_SRC_ALPHA = 770;
    public static final int GL_ONE = 1;
    public static final int GL_ONE_MINUS_DST_ALPHA = 773;
    public static final int GL_DST_ALPHA = 772;
    public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
    public static final int GL_COMPILE = 4864;
    public static final int GL_MAX_TEXTURE_SIZE = 3379;
    public static final int GL_COLOR_BUFFER_BIT = 16384;
    public static final int GL_DEPTH_BUFFER_BIT = 256;
    public static final int GL_BLEND = 3042;
    public static final int GL_COLOR_CLEAR_VALUE = 3106;
    public static final int GL_LINE_WIDTH = 2849;
    public static final int GL_CLIP_PLANE0 = 12288;
    public static final int GL_CLIP_PLANE1 = 12289;
    public static final int GL_CLIP_PLANE2 = 12290;
    public static final int GL_CLIP_PLANE3 = 12291;
    public static final int GL_COMPILE_AND_EXECUTE = 4865;
    public static final int GL_RGBA8 = 6408;
    public static final int GL_RGBA16 = 32859;
    public static final int GL_BGRA = 32993;
    public static final int GL_MIRROR_CLAMP_TO_EDGE_EXT = 34627;
    public static final int GL_TEXTURE_WRAP_S = 10242;
    public static final int GL_TEXTURE_WRAP_T = 10243;
    public static final int GL_CLAMP = 10496;
    public static final int GL_COLOR_SUM_EXT = 33880;
    public static final int GL_ALWAYS = 519;
    public static final int GL_DEPTH_TEST = 2929;
    public static final int GL_NOTEQUAL = 517;
    public static final int GL_EQUAL = 514;
    public static final int GL_SRC_COLOR = 768;
    public static final int GL_ONE_MINUS_SRC_COLOR = 769;
    public static final int GL_MODELVIEW_MATRIX = 2982;

    public void flush();

    public void initDisplay(int var1, int var2);

    public void enterOrtho(int var1, int var2);

    public void glClearColor(float var1, float var2, float var3, float var4);

    public void glClipPlane(int var1, DoubleBuffer var2);

    public void glScissor(int var1, int var2, int var3, int var4);

    public void glLineWidth(float var1);

    public void glClear(int var1);

    public void glColorMask(boolean var1, boolean var2, boolean var3, boolean var4);

    public void glLoadIdentity();

    public void glGetInteger(int var1, IntBuffer var2);

    public void glGetFloat(int var1, FloatBuffer var2);

    public void glEnable(int var1);

    public void glDisable(int var1);

    public void glBindTexture(int var1, int var2);

    public void glGetTexImage(int var1, int var2, int var3, int var4, ByteBuffer var5);

    public void glDeleteTextures(IntBuffer var1);

    public void glColor4f(float var1, float var2, float var3, float var4);

    public void glTexCoord2f(float var1, float var2);

    public void glVertex3f(float var1, float var2, float var3);

    public void glVertex2f(float var1, float var2);

    public void glRotatef(float var1, float var2, float var3, float var4);

    public void glTranslatef(float var1, float var2, float var3);

    public void glBegin(int var1);

    public void glEnd();

    public void glTexEnvi(int var1, int var2, int var3);

    public void glPointSize(float var1);

    public void glScalef(float var1, float var2, float var3);

    public void glPushMatrix();

    public void glPopMatrix();

    public void glBlendFunc(int var1, int var2);

    public int glGenLists(int var1);

    public void glNewList(int var1, int var2);

    public void glEndList();

    public void glCallList(int var1);

    public void glCopyTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

    public void glReadPixels(int var1, int var2, int var3, int var4, int var5, int var6, ByteBuffer var7);

    public void glTexParameteri(int var1, int var2, int var3);

    public float[] getCurrentColor();

    public void glDeleteLists(int var1, int var2);

    public void glDepthMask(boolean var1);

    public void glClearDepth(float var1);

    public void glDepthFunc(int var1);

    public void setGlobalAlphaScale(float var1);

    public void glLoadMatrix(FloatBuffer var1);

    public void glGenTextures(IntBuffer var1);

    public void glGetError();

    public void glTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, ByteBuffer var9);

    public void glTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, ByteBuffer var9);

    public boolean canTextureMirrorClamp();

    public boolean canSecondaryColor();

    public void glSecondaryColor3ubEXT(byte var1, byte var2, byte var3);
}

