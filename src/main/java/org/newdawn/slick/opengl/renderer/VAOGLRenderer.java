/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 */
package org.newdawn.slick.opengl.renderer;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer;

public class VAOGLRenderer
extends ImmediateModeOGLRenderer {
    private static final int TOLERANCE = 20;
    public static final int NONE = -1;
    public static final int MAX_VERTS = 5000;
    private int currentType = -1;
    private float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    private float[] tex = new float[]{0.0f, 0.0f};
    private int vertIndex;
    private float[] verts = new float[15000];
    private float[] cols = new float[20000];
    private float[] texs = new float[15000];
    private FloatBuffer vertices = BufferUtils.createFloatBuffer((int)15000);
    private FloatBuffer colors = BufferUtils.createFloatBuffer((int)20000);
    private FloatBuffer textures = BufferUtils.createFloatBuffer((int)10000);
    private int listMode = 0;

    @Override
    public void initDisplay(int width, int height) {
        super.initDisplay(width, height);
        this.startBuffer();
        GL11.glEnableClientState((int)32884);
        GL11.glEnableClientState((int)32888);
        GL11.glEnableClientState((int)32886);
    }

    private void startBuffer() {
        this.vertIndex = 0;
    }

    private void flushBuffer() {
        if (this.vertIndex == 0) {
            return;
        }
        if (this.currentType == -1) {
            return;
        }
        if (this.vertIndex < 20) {
            GL11.glBegin((int)this.currentType);
            for (int i = 0; i < this.vertIndex; ++i) {
                GL11.glColor4f((float)this.cols[i * 4 + 0], (float)this.cols[i * 4 + 1], (float)this.cols[i * 4 + 2], (float)this.cols[i * 4 + 3]);
                GL11.glTexCoord2f((float)this.texs[i * 2 + 0], (float)this.texs[i * 2 + 1]);
                GL11.glVertex3f((float)this.verts[i * 3 + 0], (float)this.verts[i * 3 + 1], (float)this.verts[i * 3 + 2]);
            }
            GL11.glEnd();
            this.currentType = -1;
            return;
        }
        this.vertices.clear();
        this.colors.clear();
        this.textures.clear();
        this.vertices.put(this.verts, 0, this.vertIndex * 3);
        this.colors.put(this.cols, 0, this.vertIndex * 4);
        this.textures.put(this.texs, 0, this.vertIndex * 2);
        this.vertices.flip();
        this.colors.flip();
        this.textures.flip();
        GL11.glVertexPointer((int)3, (int)0, (FloatBuffer)this.vertices);
        GL11.glColorPointer((int)4, (int)0, (FloatBuffer)this.colors);
        GL11.glTexCoordPointer((int)2, (int)0, (FloatBuffer)this.textures);
        GL11.glDrawArrays((int)this.currentType, (int)0, (int)this.vertIndex);
        this.currentType = -1;
    }

    private void applyBuffer() {
        if (this.listMode > 0) {
            return;
        }
        if (this.vertIndex != 0) {
            this.flushBuffer();
            this.startBuffer();
        }
        super.glColor4f(this.color[0], this.color[1], this.color[2], this.color[3]);
    }

    @Override
    public void flush() {
        super.flush();
        this.applyBuffer();
    }

    @Override
    public void glBegin(int geomType) {
        if (this.listMode > 0) {
            super.glBegin(geomType);
            return;
        }
        if (this.currentType != geomType) {
            this.applyBuffer();
            this.currentType = geomType;
        }
    }

    @Override
    public void glColor4f(float r, float g, float b, float a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a *= this.alphaScale;
        if (this.listMode > 0) {
            super.glColor4f(r, g, b, a);
            return;
        }
    }

    @Override
    public void glEnd() {
        if (this.listMode > 0) {
            super.glEnd();
            return;
        }
    }

    @Override
    public void glTexCoord2f(float u, float v) {
        if (this.listMode > 0) {
            super.glTexCoord2f(u, v);
            return;
        }
        this.tex[0] = u;
        this.tex[1] = v;
    }

    @Override
    public void glVertex2f(float x, float y) {
        if (this.listMode > 0) {
            super.glVertex2f(x, y);
            return;
        }
        this.glVertex3f(x, y, 0.0f);
    }

    @Override
    public void glVertex3f(float x, float y, float z) {
        if (this.listMode > 0) {
            super.glVertex3f(x, y, z);
            return;
        }
        this.verts[this.vertIndex * 3 + 0] = x;
        this.verts[this.vertIndex * 3 + 1] = y;
        this.verts[this.vertIndex * 3 + 2] = z;
        this.cols[this.vertIndex * 4 + 0] = this.color[0];
        this.cols[this.vertIndex * 4 + 1] = this.color[1];
        this.cols[this.vertIndex * 4 + 2] = this.color[2];
        this.cols[this.vertIndex * 4 + 3] = this.color[3];
        this.texs[this.vertIndex * 2 + 0] = this.tex[0];
        this.texs[this.vertIndex * 2 + 1] = this.tex[1];
        ++this.vertIndex;
        if (this.vertIndex > 4950 && this.isSplittable(this.vertIndex, this.currentType)) {
            int type = this.currentType;
            this.applyBuffer();
            this.currentType = type;
        }
    }

    private boolean isSplittable(int count, int type) {
        switch (type) {
            case 7: {
                return count % 4 == 0;
            }
            case 4: {
                return count % 3 == 0;
            }
            case 6913: {
                return count % 2 == 0;
            }
        }
        return false;
    }

    @Override
    public void glBindTexture(int target, int id) {
        this.applyBuffer();
        super.glBindTexture(target, id);
    }

    @Override
    public void glBlendFunc(int src, int dest) {
        this.applyBuffer();
        super.glBlendFunc(src, dest);
    }

    @Override
    public void glCallList(int id) {
        this.applyBuffer();
        super.glCallList(id);
    }

    @Override
    public void glClear(int value) {
        this.applyBuffer();
        super.glClear(value);
    }

    @Override
    public void glClipPlane(int plane, DoubleBuffer buffer) {
        this.applyBuffer();
        super.glClipPlane(plane, buffer);
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        this.applyBuffer();
        super.glColorMask(red, green, blue, alpha);
    }

    @Override
    public void glDisable(int item) {
        this.applyBuffer();
        super.glDisable(item);
    }

    @Override
    public void glEnable(int item) {
        this.applyBuffer();
        super.glEnable(item);
    }

    @Override
    public void glLineWidth(float width) {
        this.applyBuffer();
        super.glLineWidth(width);
    }

    @Override
    public void glPointSize(float size) {
        this.applyBuffer();
        super.glPointSize(size);
    }

    @Override
    public void glPopMatrix() {
        this.applyBuffer();
        super.glPopMatrix();
    }

    @Override
    public void glPushMatrix() {
        this.applyBuffer();
        super.glPushMatrix();
    }

    @Override
    public void glRotatef(float angle, float x, float y, float z) {
        this.applyBuffer();
        super.glRotatef(angle, x, y, z);
    }

    @Override
    public void glScalef(float x, float y, float z) {
        this.applyBuffer();
        super.glScalef(x, y, z);
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        this.applyBuffer();
        super.glScissor(x, y, width, height);
    }

    @Override
    public void glTexEnvi(int target, int mode, int value) {
        this.applyBuffer();
        super.glTexEnvi(target, mode, value);
    }

    @Override
    public void glTranslatef(float x, float y, float z) {
        this.applyBuffer();
        super.glTranslatef(x, y, z);
    }

    @Override
    public void glEndList() {
        --this.listMode;
        super.glEndList();
    }

    @Override
    public void glNewList(int id, int option) {
        ++this.listMode;
        super.glNewList(id, option);
    }

    @Override
    public float[] getCurrentColor() {
        return this.color;
    }

    @Override
    public void glLoadMatrix(FloatBuffer buffer) {
        this.flushBuffer();
        super.glLoadMatrix(buffer);
    }
}

