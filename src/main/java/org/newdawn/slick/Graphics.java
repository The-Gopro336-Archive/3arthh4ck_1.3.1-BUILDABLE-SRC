/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 */
package org.newdawn.slick;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.LineStripRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;

public class Graphics {
    protected static SGL GL = Renderer.get();
    private static LineStripRenderer LSR = Renderer.getLineStripRenderer();
    public static int MODE_NORMAL = 1;
    public static int MODE_ALPHA_MAP = 2;
    public static int MODE_ALPHA_BLEND = 3;
    public static int MODE_COLOR_MULTIPLY = 4;
    public static int MODE_ADD = 5;
    public static int MODE_SCREEN = 6;
    private static final int DEFAULT_SEGMENTS = 50;
    protected static Graphics currentGraphics = null;
    protected static Font DEFAULT_FONT;
    private float sx = 1.0f;
    private float sy = 1.0f;
    private Font font;
    private Color currentColor = Color.white;
    protected int screenWidth;
    protected int screenHeight;
    private boolean pushed;
    private Rectangle clip;
    private DoubleBuffer worldClip = BufferUtils.createDoubleBuffer((int)4);
    private ByteBuffer readBuffer = BufferUtils.createByteBuffer((int)4);
    private boolean antialias;
    private Rectangle worldClipRecord;
    private int currentDrawingMode = MODE_NORMAL;
    private float lineWidth = 1.0f;
    private ArrayList stack = new ArrayList();
    private int stackIndex;

    public static void setCurrent(Graphics current) {
        if (currentGraphics != current) {
            if (currentGraphics != null) {
                currentGraphics.disable();
            }
            currentGraphics = current;
            currentGraphics.enable();
        }
    }

    public Graphics() {
    }

    public Graphics(int width, int height) {
        if (DEFAULT_FONT == null) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        DEFAULT_FONT = new AngelCodeFont("org/newdawn/slick/data/defaultfont.fnt", "org/newdawn/slick/data/defaultfont.png");
                    }
                    catch (SlickException e) {
                        Log.error(e);
                    }
                    return null;
                }
            });
        }
        this.font = DEFAULT_FONT;
        this.screenWidth = width;
        this.screenHeight = height;
    }

    void setDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void setDrawMode(int mode) {
        this.predraw();
        this.currentDrawingMode = mode;
        if (this.currentDrawingMode == MODE_NORMAL) {
            GL.glEnable(3042);
            GL.glColorMask(true, true, true, true);
            GL.glBlendFunc(770, 771);
        }
        if (this.currentDrawingMode == MODE_ALPHA_MAP) {
            GL.glDisable(3042);
            GL.glColorMask(false, false, false, true);
        }
        if (this.currentDrawingMode == MODE_ALPHA_BLEND) {
            GL.glEnable(3042);
            GL.glColorMask(true, true, true, false);
            GL.glBlendFunc(772, 773);
        }
        if (this.currentDrawingMode == MODE_COLOR_MULTIPLY) {
            GL.glEnable(3042);
            GL.glColorMask(true, true, true, true);
            GL.glBlendFunc(769, 768);
        }
        if (this.currentDrawingMode == MODE_ADD) {
            GL.glEnable(3042);
            GL.glColorMask(true, true, true, true);
            GL.glBlendFunc(1, 1);
        }
        if (this.currentDrawingMode == MODE_SCREEN) {
            GL.glEnable(3042);
            GL.glColorMask(true, true, true, true);
            GL.glBlendFunc(1, 769);
        }
        this.postdraw();
    }

    public void clearAlphaMap() {
        this.pushTransform();
        GL.glLoadIdentity();
        int originalMode = this.currentDrawingMode;
        this.setDrawMode(MODE_ALPHA_MAP);
        this.setColor(new Color(0, 0, 0, 0));
        this.fillRect(0.0f, 0.0f, this.screenWidth, this.screenHeight);
        this.setColor(this.currentColor);
        this.setDrawMode(originalMode);
        this.popTransform();
    }

    private void predraw() {
        Graphics.setCurrent(this);
    }

    private void postdraw() {
    }

    protected void enable() {
    }

    public void flush() {
        if (currentGraphics == this) {
            currentGraphics.disable();
            currentGraphics = null;
        }
    }

    protected void disable() {
    }

    public Font getFont() {
        return this.font;
    }

    public void setBackground(Color color) {
        this.predraw();
        GL.glClearColor(color.r, color.g, color.b, color.a);
        this.postdraw();
    }

    public Color getBackground() {
        this.predraw();
        FloatBuffer buffer = BufferUtils.createFloatBuffer((int)16);
        GL.glGetFloat(3106, buffer);
        this.postdraw();
        return new Color(buffer);
    }

    public void clear() {
        this.predraw();
        GL.glClear(16384);
        this.postdraw();
    }

    public void resetTransform() {
        this.sx = 1.0f;
        this.sy = 1.0f;
        if (this.pushed) {
            this.predraw();
            GL.glPopMatrix();
            this.pushed = false;
            this.postdraw();
        }
    }

    private void checkPush() {
        if (!this.pushed) {
            this.predraw();
            GL.glPushMatrix();
            this.pushed = true;
            this.postdraw();
        }
    }

    public void scale(float sx, float sy) {
        this.sx *= sx;
        this.sy *= sy;
        this.checkPush();
        this.predraw();
        GL.glScalef(sx, sy, 1.0f);
        this.postdraw();
    }

    public void rotate(float rx, float ry, float ang) {
        this.checkPush();
        this.predraw();
        this.translate(rx, ry);
        GL.glRotatef(ang, 0.0f, 0.0f, 1.0f);
        this.translate(-rx, -ry);
        this.postdraw();
    }

    public void translate(float x, float y) {
        this.checkPush();
        this.predraw();
        GL.glTranslatef(x, y, 0.0f);
        this.postdraw();
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void resetFont() {
        this.font = DEFAULT_FONT;
    }

    public void setColor(Color color) {
        if (color == null) {
            return;
        }
        this.currentColor = new Color(color);
        this.predraw();
        this.currentColor.bind();
        this.postdraw();
    }

    public Color getColor() {
        return new Color(this.currentColor);
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        float lineWidth = this.lineWidth - 1.0f;
        if (LSR.applyGLLineFixes()) {
            if (x1 == x2) {
                if (y1 > y2) {
                    float temp = y2;
                    y2 = y1;
                    y1 = temp;
                }
                float step = 1.0f / this.sy;
                this.fillRect(x1 - (lineWidth /= this.sy) / 2.0f, y1 - lineWidth / 2.0f, lineWidth + step, y2 - y1 + lineWidth + step);
                return;
            }
            if (y1 == y2) {
                if (x1 > x2) {
                    float temp = x2;
                    x2 = x1;
                    x1 = temp;
                }
                float step = 1.0f / this.sx;
                this.fillRect(x1 - (lineWidth /= this.sx) / 2.0f, y1 - lineWidth / 2.0f, x2 - x1 + lineWidth + step, lineWidth + step);
                return;
            }
        }
        this.predraw();
        this.currentColor.bind();
        TextureImpl.bindNone();
        LSR.start();
        LSR.vertex(x1, y1);
        LSR.vertex(x2, y2);
        LSR.end();
        this.postdraw();
    }

    public void draw(Shape shape, ShapeFill fill) {
        this.predraw();
        TextureImpl.bindNone();
        ShapeRenderer.draw(shape, fill);
        this.currentColor.bind();
        this.postdraw();
    }

    public void fill(Shape shape, ShapeFill fill) {
        this.predraw();
        TextureImpl.bindNone();
        ShapeRenderer.fill(shape, fill);
        this.currentColor.bind();
        this.postdraw();
    }

    public void draw(Shape shape) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        ShapeRenderer.draw(shape);
        this.postdraw();
    }

    public void fill(Shape shape) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        ShapeRenderer.fill(shape);
        this.postdraw();
    }

    public void texture(Shape shape, Image image) {
        this.texture(shape, image, 0.01f, 0.01f, false);
    }

    public void texture(Shape shape, Image image, ShapeFill fill) {
        this.texture(shape, image, 0.01f, 0.01f, fill);
    }

    public void texture(Shape shape, Image image, boolean fit) {
        if (fit) {
            this.texture(shape, image, 1.0f, 1.0f, true);
        } else {
            this.texture(shape, image, 0.01f, 0.01f, false);
        }
    }

    public void texture(Shape shape, Image image, float scaleX, float scaleY) {
        this.texture(shape, image, scaleX, scaleY, false);
    }

    public void texture(Shape shape, Image image, float scaleX, float scaleY, boolean fit) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        if (fit) {
            ShapeRenderer.textureFit(shape, image, scaleX, scaleY);
        } else {
            ShapeRenderer.texture(shape, image, scaleX, scaleY);
        }
        this.postdraw();
    }

    public void texture(Shape shape, Image image, float scaleX, float scaleY, ShapeFill fill) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        ShapeRenderer.texture(shape, image, scaleX, scaleY, fill);
        this.postdraw();
    }

    public void drawRect(float x1, float y1, float width, float height) {
        float lineWidth = this.getLineWidth();
        this.drawLine(x1, y1, x1 + width, y1);
        this.drawLine(x1 + width, y1, x1 + width, y1 + height);
        this.drawLine(x1 + width, y1 + height, x1, y1 + height);
        this.drawLine(x1, y1 + height, x1, y1);
    }

    public void clearClip() {
        this.clip = null;
        this.predraw();
        GL.glDisable(3089);
        this.postdraw();
    }

    public void setWorldClip(float x, float y, float width, float height) {
        this.predraw();
        this.worldClipRecord = new Rectangle(x, y, width, height);
        GL.glEnable(12288);
        this.worldClip.put(1.0).put(0.0).put(0.0).put(-x).flip();
        GL.glClipPlane(12288, this.worldClip);
        GL.glEnable(12289);
        this.worldClip.put(-1.0).put(0.0).put(0.0).put(x + width).flip();
        GL.glClipPlane(12289, this.worldClip);
        GL.glEnable(12290);
        this.worldClip.put(0.0).put(1.0).put(0.0).put(-y).flip();
        GL.glClipPlane(12290, this.worldClip);
        GL.glEnable(12291);
        this.worldClip.put(0.0).put(-1.0).put(0.0).put(y + height).flip();
        GL.glClipPlane(12291, this.worldClip);
        this.postdraw();
    }

    public void clearWorldClip() {
        this.predraw();
        this.worldClipRecord = null;
        GL.glDisable(12288);
        GL.glDisable(12289);
        GL.glDisable(12290);
        GL.glDisable(12291);
        this.postdraw();
    }

    public void setWorldClip(Rectangle clip) {
        if (clip == null) {
            this.clearWorldClip();
        } else {
            this.setWorldClip(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight());
        }
    }

    public Rectangle getWorldClip() {
        return this.worldClipRecord;
    }

    public void setClip(int x, int y, int width, int height) {
        this.predraw();
        if (this.clip == null) {
            GL.glEnable(3089);
            this.clip = new Rectangle(x, y, width, height);
        } else {
            this.clip.setBounds(x, y, width, height);
        }
        GL.glScissor(x, this.screenHeight - y - height, width, height);
        this.postdraw();
    }

    public void setClip(Rectangle rect) {
        if (rect == null) {
            this.clearClip();
            return;
        }
        this.setClip((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
    }

    public Rectangle getClip() {
        return this.clip;
    }

    public void fillRect(float x, float y, float width, float height, Image pattern, float offX, float offY) {
        int cols = (int)Math.ceil(width / (float)pattern.getWidth()) + 2;
        int rows = (int)Math.ceil(height / (float)pattern.getHeight()) + 2;
        Rectangle preClip = this.getWorldClip();
        this.setWorldClip(x, y, width, height);
        this.predraw();
        for (int c = 0; c < cols; ++c) {
            for (int r = 0; r < rows; ++r) {
                pattern.draw((float)(c * pattern.getWidth()) + x - offX, (float)(r * pattern.getHeight()) + y - offY);
            }
        }
        this.postdraw();
        this.setWorldClip(preClip);
    }

    public void fillRect(float x1, float y1, float width, float height) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        GL.glBegin(7);
        GL.glVertex2f(x1, y1);
        GL.glVertex2f(x1 + width, y1);
        GL.glVertex2f(x1 + width, y1 + height);
        GL.glVertex2f(x1, y1 + height);
        GL.glEnd();
        this.postdraw();
    }

    public void drawOval(float x1, float y1, float width, float height) {
        this.drawOval(x1, y1, width, height, 50);
    }

    public void drawOval(float x1, float y1, float width, float height, int segments) {
        this.drawArc(x1, y1, width, height, segments, 0.0f, 360.0f);
    }

    public void drawArc(float x1, float y1, float width, float height, float start, float end) {
        this.drawArc(x1, y1, width, height, 50, start, end);
    }

    public void drawArc(float x1, float y1, float width, float height, int segments, float start, float end) {
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        while (end < start) {
            end += 360.0f;
        }
        float cx = x1 + width / 2.0f;
        float cy = y1 + height / 2.0f;
        LSR.start();
        int step = 360 / segments;
        for (int a = (int)start; a < (int)(end + (float)step); a += step) {
            float ang = a;
            if (ang > end) {
                ang = end;
            }
            float x = (float)((double)cx + FastTrig.cos(Math.toRadians(ang)) * (double)width / 2.0);
            float y = (float)((double)cy + FastTrig.sin(Math.toRadians(ang)) * (double)height / 2.0);
            LSR.vertex(x, y);
        }
        LSR.end();
        this.postdraw();
    }

    public void fillOval(float x1, float y1, float width, float height) {
        this.fillOval(x1, y1, width, height, 50);
    }

    public void fillOval(float x1, float y1, float width, float height, int segments) {
        this.fillArc(x1, y1, width, height, segments, 0.0f, 360.0f);
    }

    public void fillArc(float x1, float y1, float width, float height, float start, float end) {
        this.fillArc(x1, y1, width, height, 50, start, end);
    }

    public void fillArc(float x1, float y1, float width, float height, int segments, float start, float end) {
        float y;
        float x;
        float ang;
        int a;
        this.predraw();
        TextureImpl.bindNone();
        this.currentColor.bind();
        while (end < start) {
            end += 360.0f;
        }
        float cx = x1 + width / 2.0f;
        float cy = y1 + height / 2.0f;
        GL.glBegin(6);
        int step = 360 / segments;
        GL.glVertex2f(cx, cy);
        for (a = (int)start; a < (int)(end + (float)step); a += step) {
            ang = a;
            if (ang > end) {
                ang = end;
            }
            x = (float)((double)cx + FastTrig.cos(Math.toRadians(ang)) * (double)width / 2.0);
            y = (float)((double)cy + FastTrig.sin(Math.toRadians(ang)) * (double)height / 2.0);
            GL.glVertex2f(x, y);
        }
        GL.glEnd();
        if (this.antialias) {
            GL.glBegin(6);
            GL.glVertex2f(cx, cy);
            if (end != 360.0f) {
                end -= 10.0f;
            }
            for (a = (int)start; a < (int)(end + (float)step); a += step) {
                ang = a;
                if (ang > end) {
                    ang = end;
                }
                x = (float)((double)cx + FastTrig.cos(Math.toRadians(ang + 10.0f)) * (double)width / 2.0);
                y = (float)((double)cy + FastTrig.sin(Math.toRadians(ang + 10.0f)) * (double)height / 2.0);
                GL.glVertex2f(x, y);
            }
            GL.glEnd();
        }
        this.postdraw();
    }

    public void drawRoundRect(float x, float y, float width, float height, int cornerRadius) {
        this.drawRoundRect(x, y, width, height, cornerRadius, 50);
    }

    public void drawRoundRect(float x, float y, float width, float height, int cornerRadius, int segs) {
        if (cornerRadius < 0) {
            throw new IllegalArgumentException("corner radius must be > 0");
        }
        if (cornerRadius == 0) {
            this.drawRect(x, y, width, height);
            return;
        }
        int mr = (int)Math.min(width, height) / 2;
        if (cornerRadius > mr) {
            cornerRadius = mr;
        }
        this.drawLine(x + (float)cornerRadius, y, x + width - (float)cornerRadius, y);
        this.drawLine(x, y + (float)cornerRadius, x, y + height - (float)cornerRadius);
        this.drawLine(x + width, y + (float)cornerRadius, x + width, y + height - (float)cornerRadius);
        this.drawLine(x + (float)cornerRadius, y + height, x + width - (float)cornerRadius, y + height);
        float d = cornerRadius * 2;
        this.drawArc(x + width - d, y + height - d, d, d, segs, 0.0f, 90.0f);
        this.drawArc(x, y + height - d, d, d, segs, 90.0f, 180.0f);
        this.drawArc(x + width - d, y, d, d, segs, 270.0f, 360.0f);
        this.drawArc(x, y, d, d, segs, 180.0f, 270.0f);
    }

    public void fillRoundRect(float x, float y, float width, float height, int cornerRadius) {
        this.fillRoundRect(x, y, width, height, cornerRadius, 50);
    }

    public void fillRoundRect(float x, float y, float width, float height, int cornerRadius, int segs) {
        if (cornerRadius < 0) {
            throw new IllegalArgumentException("corner radius must be > 0");
        }
        if (cornerRadius == 0) {
            this.fillRect(x, y, width, height);
            return;
        }
        int mr = (int)Math.min(width, height) / 2;
        if (cornerRadius > mr) {
            cornerRadius = mr;
        }
        float d = cornerRadius * 2;
        this.fillRect(x + (float)cornerRadius, y, width - d, cornerRadius);
        this.fillRect(x, y + (float)cornerRadius, cornerRadius, height - d);
        this.fillRect(x + width - (float)cornerRadius, y + (float)cornerRadius, cornerRadius, height - d);
        this.fillRect(x + (float)cornerRadius, y + height - (float)cornerRadius, width - d, cornerRadius);
        this.fillRect(x + (float)cornerRadius, y + (float)cornerRadius, width - d, height - d);
        this.fillArc(x + width - d, y + height - d, d, d, segs, 0.0f, 90.0f);
        this.fillArc(x, y + height - d, d, d, segs, 90.0f, 180.0f);
        this.fillArc(x + width - d, y, d, d, segs, 270.0f, 360.0f);
        this.fillArc(x, y, d, d, segs, 180.0f, 270.0f);
    }

    public void setLineWidth(float width) {
        this.predraw();
        this.lineWidth = width;
        LSR.setWidth(width);
        GL.glPointSize(width);
        this.postdraw();
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void resetLineWidth() {
        this.predraw();
        Renderer.getLineStripRenderer().setWidth(1.0f);
        GL.glLineWidth(1.0f);
        GL.glPointSize(1.0f);
        this.postdraw();
    }

    public void setAntiAlias(boolean anti) {
        this.predraw();
        this.antialias = anti;
        LSR.setAntiAlias(anti);
        if (anti) {
            GL.glEnable(2881);
        } else {
            GL.glDisable(2881);
        }
        this.postdraw();
    }

    public boolean isAntiAlias() {
        return this.antialias;
    }

    public void drawString(String str, float x, float y) {
        this.predraw();
        this.font.drawString(x, y, str, this.currentColor);
        this.postdraw();
    }

    public void drawImage(Image image, float x, float y, Color col) {
        this.predraw();
        image.draw(x, y, col);
        this.currentColor.bind();
        this.postdraw();
    }

    public void drawAnimation(Animation anim, float x, float y) {
        this.drawAnimation(anim, x, y, Color.white);
    }

    public void drawAnimation(Animation anim, float x, float y, Color col) {
        this.predraw();
        anim.draw(x, y, col);
        this.currentColor.bind();
        this.postdraw();
    }

    public void drawImage(Image image, float x, float y) {
        this.drawImage(image, x, y, Color.white);
    }

    public void drawImage(Image image, float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
        this.predraw();
        image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2);
        this.currentColor.bind();
        this.postdraw();
    }

    public void drawImage(Image image, float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
        this.drawImage(image, x, y, x + (float)image.getWidth(), y + (float)image.getHeight(), srcx, srcy, srcx2, srcy2);
    }

    public void copyArea(Image target, int x, int y) {
        int format = target.getTexture().hasAlpha() ? 6408 : 6407;
        target.bind();
        GL.glCopyTexImage2D(3553, 0, format, x, this.screenHeight - (y + target.getHeight()), target.getTexture().getTextureWidth(), target.getTexture().getTextureHeight(), 0);
        target.ensureInverted();
    }

    private int translate(byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }

    public Color getPixel(int x, int y) {
        this.predraw();
        GL.glReadPixels(x, this.screenHeight - y, 1, 1, 6408, 5121, this.readBuffer);
        this.postdraw();
        return new Color(this.translate(this.readBuffer.get(0)), this.translate(this.readBuffer.get(1)), this.translate(this.readBuffer.get(2)), this.translate(this.readBuffer.get(3)));
    }

    public void getArea(int x, int y, int width, int height, ByteBuffer target) {
        if (target.capacity() < width * height * 4) {
            throw new IllegalArgumentException("Byte buffer provided to get area is not big enough");
        }
        this.predraw();
        GL.glReadPixels(x, this.screenHeight - y - height, width, height, 6408, 5121, target);
        this.postdraw();
    }

    public void drawImage(Image image, float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color col) {
        this.predraw();
        image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2, col);
        this.currentColor.bind();
        this.postdraw();
    }

    public void drawImage(Image image, float x, float y, float srcx, float srcy, float srcx2, float srcy2, Color col) {
        this.drawImage(image, x, y, x + (float)image.getWidth(), y + (float)image.getHeight(), srcx, srcy, srcx2, srcy2, col);
    }

    public void drawGradientLine(float x1, float y1, float red1, float green1, float blue1, float alpha1, float x2, float y2, float red2, float green2, float blue2, float alpha2) {
        this.predraw();
        TextureImpl.bindNone();
        GL.glBegin(1);
        GL.glColor4f(red1, green1, blue1, alpha1);
        GL.glVertex2f(x1, y1);
        GL.glColor4f(red2, green2, blue2, alpha2);
        GL.glVertex2f(x2, y2);
        GL.glEnd();
        this.postdraw();
    }

    public void drawGradientLine(float x1, float y1, Color Color1, float x2, float y2, Color Color2) {
        this.predraw();
        TextureImpl.bindNone();
        GL.glBegin(1);
        Color1.bind();
        GL.glVertex2f(x1, y1);
        Color2.bind();
        GL.glVertex2f(x2, y2);
        GL.glEnd();
        this.postdraw();
    }

    public void pushTransform() {
        FloatBuffer buffer;
        this.predraw();
        if (this.stackIndex >= this.stack.size()) {
            buffer = BufferUtils.createFloatBuffer((int)18);
            this.stack.add(buffer);
        } else {
            buffer = (FloatBuffer)this.stack.get(this.stackIndex);
        }
        GL.glGetFloat(2982, buffer);
        buffer.put(16, this.sx);
        buffer.put(17, this.sy);
        ++this.stackIndex;
        this.postdraw();
    }

    public void popTransform() {
        if (this.stackIndex == 0) {
            throw new RuntimeException("Attempt to pop a transform that hasn't be pushed");
        }
        this.predraw();
        --this.stackIndex;
        FloatBuffer oldBuffer = (FloatBuffer)this.stack.get(this.stackIndex);
        GL.glLoadMatrix(oldBuffer);
        this.sx = oldBuffer.get(16);
        this.sy = oldBuffer.get(17);
        this.postdraw();
    }

    public void destroy() {
    }
}

