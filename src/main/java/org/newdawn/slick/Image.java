/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import java.io.IOException;
import java.io.InputStream;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

public class Image
implements Renderable {
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    protected static SGL GL = Renderer.get();
    protected static Image inUse;
    public static final int FILTER_LINEAR = 1;
    public static final int FILTER_NEAREST = 2;
    protected Texture texture;
    protected int width;
    protected int height;
    protected float textureWidth;
    protected float textureHeight;
    protected float textureOffsetX;
    protected float textureOffsetY;
    protected float angle;
    protected float alpha = 1.0f;
    protected String ref;
    protected boolean inited = false;
    protected byte[] pixelData;
    protected boolean destroyed;
    protected float centerX;
    protected float centerY;
    protected String name;
    protected Color[] corners;
    private int filter = 9729;
    private boolean flipped;
    private Color transparent;

    protected Image(Image other) {
        this.width = other.getWidth();
        this.height = other.getHeight();
        this.texture = other.texture;
        this.textureWidth = other.textureWidth;
        this.textureHeight = other.textureHeight;
        this.ref = other.ref;
        this.textureOffsetX = other.textureOffsetX;
        this.textureOffsetY = other.textureOffsetY;
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.inited = true;
    }

    protected Image() {
    }

    public Image(Texture texture) {
        this.texture = texture;
        this.ref = texture.toString();
        this.clampTexture();
    }

    public Image(String ref) throws SlickException {
        this(ref, false);
    }

    public Image(String ref, Color trans) throws SlickException {
        this(ref, false, 1, trans);
    }

    public Image(String ref, boolean flipped) throws SlickException {
        this(ref, flipped, 1);
    }

    public Image(String ref, boolean flipped, int filter) throws SlickException {
        this(ref, flipped, filter, null);
    }

    public Image(String ref, boolean flipped, int f, Color transparent) throws SlickException {
        this.filter = f == 1 ? 9729 : 9728;
        this.transparent = transparent;
        this.flipped = flipped;
        try {
            this.ref = ref;
            int[] trans = null;
            if (transparent != null) {
                trans = new int[]{(int)(transparent.r * 255.0f), (int)(transparent.g * 255.0f), (int)(transparent.b * 255.0f)};
            }
            this.texture = InternalTextureLoader.get().getTexture(ref, flipped, this.filter, trans);
        }
        catch (IOException e) {
            Log.error(e);
            throw new SlickException("Failed to load image from: " + ref, e);
        }
    }

    public void setFilter(int f) {
        this.filter = f == 1 ? 9729 : 9728;
        this.texture.bind();
        GL.glTexParameteri(3553, 10241, this.filter);
        GL.glTexParameteri(3553, 10240, this.filter);
    }

    public Image(int width, int height) throws SlickException {
        this(width, height, 2);
    }

    public Image(int width, int height, int f) throws SlickException {
        this.ref = super.toString();
        this.filter = f == 1 ? 9729 : 9728;
        try {
            this.texture = InternalTextureLoader.get().createTexture(width, height, this.filter);
        }
        catch (IOException e) {
            Log.error(e);
            throw new SlickException("Failed to create empty image " + width + "x" + height);
        }
        this.init();
    }

    public Image(InputStream in, String ref, boolean flipped) throws SlickException {
        this(in, ref, flipped, 1);
    }

    public Image(InputStream in, String ref, boolean flipped, int filter) throws SlickException {
        this.load(in, ref, flipped, filter, null);
    }

    Image(ImageBuffer buffer) {
        this(buffer, 1);
        TextureImpl.bindNone();
    }

    Image(ImageBuffer buffer, int filter) {
        this((ImageData)buffer, filter);
        TextureImpl.bindNone();
    }

    public Image(ImageData data) {
        this(data, 1);
    }

    public Image(ImageData data, int f) {
        try {
            this.filter = f == 1 ? 9729 : 9728;
            this.texture = InternalTextureLoader.get().getTexture(data, this.filter);
            this.ref = this.texture.toString();
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    public int getFilter() {
        return this.filter;
    }

    public String getResourceReference() {
        return this.ref;
    }

    public void setImageColor(float r, float g, float b, float a) {
        this.setColor(0, r, g, b, a);
        this.setColor(1, r, g, b, a);
        this.setColor(3, r, g, b, a);
        this.setColor(2, r, g, b, a);
    }

    public void setImageColor(float r, float g, float b) {
        this.setColor(0, r, g, b);
        this.setColor(1, r, g, b);
        this.setColor(3, r, g, b);
        this.setColor(2, r, g, b);
    }

    public void setColor(int corner, float r, float g, float b, float a) {
        if (this.corners == null) {
            this.corners = new Color[]{new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f)};
        }
        this.corners[corner].r = r;
        this.corners[corner].g = g;
        this.corners[corner].b = b;
        this.corners[corner].a = a;
    }

    public void setColor(int corner, float r, float g, float b) {
        if (this.corners == null) {
            this.corners = new Color[]{new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f)};
        }
        this.corners[corner].r = r;
        this.corners[corner].g = g;
        this.corners[corner].b = b;
    }

    public void clampTexture() {
        if (GL.canTextureMirrorClamp()) {
            GL.glTexParameteri(3553, 10242, 34627);
            GL.glTexParameteri(3553, 10243, 34627);
        } else {
            GL.glTexParameteri(3553, 10242, 10496);
            GL.glTexParameteri(3553, 10243, 10496);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Graphics getGraphics() throws SlickException {
        return GraphicsFactory.getGraphicsForImage(this);
    }

    private void load(InputStream in, String ref, boolean flipped, int f, Color transparent) throws SlickException {
        this.filter = f == 1 ? 9729 : 9728;
        try {
            this.ref = ref;
            int[] trans = null;
            if (transparent != null) {
                trans = new int[]{(int)(transparent.r * 255.0f), (int)(transparent.g * 255.0f), (int)(transparent.b * 255.0f)};
            }
            this.texture = InternalTextureLoader.get().getTexture(in, ref, flipped, this.filter, trans);
        }
        catch (IOException e) {
            Log.error(e);
            throw new SlickException("Failed to load image from: " + ref, e);
        }
    }

    public void bind() {
        this.texture.bind();
    }

    protected void reinit() {
        this.inited = false;
        this.init();
    }

    protected final void init() {
        if (this.inited) {
            return;
        }
        this.inited = true;
        if (this.texture != null) {
            this.width = this.texture.getImageWidth();
            this.height = this.texture.getImageHeight();
            this.textureOffsetX = 0.0f;
            this.textureOffsetY = 0.0f;
            this.textureWidth = this.texture.getWidth();
            this.textureHeight = this.texture.getHeight();
        }
        this.initImpl();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
    }

    protected void initImpl() {
    }

    public void draw() {
        this.draw(0.0f, 0.0f);
    }

    public void drawCentered(float x, float y) {
        this.draw(x - (float)(this.getWidth() / 2), y - (float)(this.getHeight() / 2));
    }

    @Override
    public void draw(float x, float y) {
        this.init();
        this.draw(x, y, (float)this.width, this.height);
    }

    @Override
    public void draw(float x, float y, Color filter) {
        this.init();
        this.draw(x, y, this.width, this.height, filter);
    }

    public void drawEmbedded(float x, float y, float width, float height) {
        this.init();
        if (this.corners == null) {
            GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY);
            GL.glVertex3f(x, y, 0.0f);
            GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY + this.textureHeight);
            GL.glVertex3f(x, y + height, 0.0f);
            GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY + this.textureHeight);
            GL.glVertex3f(x + width, y + height, 0.0f);
            GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY);
            GL.glVertex3f(x + width, y, 0.0f);
        } else {
            this.corners[0].bind();
            GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY);
            GL.glVertex3f(x, y, 0.0f);
            this.corners[3].bind();
            GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY + this.textureHeight);
            GL.glVertex3f(x, y + height, 0.0f);
            this.corners[2].bind();
            GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY + this.textureHeight);
            GL.glVertex3f(x + width, y + height, 0.0f);
            this.corners[1].bind();
            GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY);
            GL.glVertex3f(x + width, y, 0.0f);
        }
    }

    public float getTextureOffsetX() {
        this.init();
        return this.textureOffsetX;
    }

    public float getTextureOffsetY() {
        this.init();
        return this.textureOffsetY;
    }

    public float getTextureWidth() {
        this.init();
        return this.textureWidth;
    }

    public float getTextureHeight() {
        this.init();
        return this.textureHeight;
    }

    public void draw(float x, float y, float scale) {
        this.init();
        this.draw(x, y, (float)this.width * scale, (float)this.height * scale, Color.white);
    }

    public void draw(float x, float y, float scale, Color filter) {
        this.init();
        this.draw(x, y, (float)this.width * scale, (float)this.height * scale, filter);
    }

    @Override
    public void draw(float x, float y, float width, float height) {
        this.init();
        this.draw(x, y, width, height, Color.white);
    }

    public void drawSheared(float x, float y, float hshear, float vshear) {
        this.drawSheared(x, y, hshear, vshear, Color.white);
    }

    public void drawSheared(float x, float y, float hshear, float vshear, Color filter) {
        if (this.alpha != 1.0f) {
            if (filter == null) {
                filter = Color.white;
            }
            filter = new Color(filter);
            filter.a *= this.alpha;
        }
        if (filter != null) {
            filter.bind();
        }
        this.texture.bind();
        GL.glTranslatef(x, y, 0.0f);
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glBegin(7);
        this.init();
        GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY);
        GL.glVertex3f(0.0f, 0.0f, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY + this.textureHeight);
        GL.glVertex3f(hshear, this.height, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY + this.textureHeight);
        GL.glVertex3f((float)this.width + hshear, (float)this.height + vshear, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY);
        GL.glVertex3f(this.width, vshear, 0.0f);
        GL.glEnd();
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(-this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glTranslatef(-x, -y, 0.0f);
    }

    @Override
    public void draw(float x, float y, float width, float height, Color filter) {
        if (this.alpha != 1.0f) {
            if (filter == null) {
                filter = Color.white;
            }
            filter = new Color(filter);
            filter.a *= this.alpha;
        }
        if (filter != null) {
            filter.bind();
        }
        this.texture.bind();
        GL.glTranslatef(x, y, 0.0f);
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glBegin(7);
        this.drawEmbedded(0.0f, 0.0f, width, height);
        GL.glEnd();
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(-this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glTranslatef(-x, -y, 0.0f);
    }

    public void drawFlash(float x, float y, float width, float height) {
        this.drawFlash(x, y, width, height, Color.white);
    }

    public void setCenterOfRotation(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }

    public float getCenterOfRotationX() {
        this.init();
        return this.centerX;
    }

    public float getCenterOfRotationY() {
        this.init();
        return this.centerY;
    }

    public void drawFlash(float x, float y, float width, float height, Color col) {
        this.init();
        col.bind();
        this.texture.bind();
        if (GL.canSecondaryColor()) {
            GL.glEnable(33880);
            GL.glSecondaryColor3ubEXT((byte)(col.r * 255.0f), (byte)(col.g * 255.0f), (byte)(col.b * 255.0f));
        }
        GL.glTexEnvi(8960, 8704, 8448);
        GL.glTranslatef(x, y, 0.0f);
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glBegin(7);
        this.drawEmbedded(0.0f, 0.0f, width, height);
        GL.glEnd();
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(-this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glTranslatef(-x, -y, 0.0f);
        if (GL.canSecondaryColor()) {
            GL.glDisable(33880);
        }
    }

    public void drawFlash(float x, float y) {
        this.drawFlash(x, y, this.getWidth(), this.getHeight());
    }

    public void setRotation(float angle) {
        this.angle = angle % 360.0f;
    }

    public float getRotation() {
        return this.angle;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void rotate(float angle) {
        this.angle += angle;
        this.angle %= 360.0f;
    }

    public Image getSubImage(int x, int y, int width, int height) {
        this.init();
        float newTextureOffsetX = (float)x / (float)this.width * this.textureWidth + this.textureOffsetX;
        float newTextureOffsetY = (float)y / (float)this.height * this.textureHeight + this.textureOffsetY;
        float newTextureWidth = (float)width / (float)this.width * this.textureWidth;
        float newTextureHeight = (float)height / (float)this.height * this.textureHeight;
        Image sub = new Image();
        sub.inited = true;
        sub.texture = this.texture;
        sub.textureOffsetX = newTextureOffsetX;
        sub.textureOffsetY = newTextureOffsetY;
        sub.textureWidth = newTextureWidth;
        sub.textureHeight = newTextureHeight;
        sub.width = width;
        sub.height = height;
        sub.ref = this.ref;
        sub.centerX = width / 2;
        sub.centerY = height / 2;
        return sub;
    }

    public void draw(float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
        this.draw(x, y, x + (float)this.width, y + (float)this.height, srcx, srcy, srcx2, srcy2);
    }

    public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
        this.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2, Color.white);
    }

    public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
        this.init();
        if (this.alpha != 1.0f) {
            if (filter == null) {
                filter = Color.white;
            }
            filter = new Color(filter);
            filter.a *= this.alpha;
        }
        filter.bind();
        this.texture.bind();
        GL.glTranslatef(x, y, 0.0f);
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glBegin(7);
        this.drawEmbedded(0.0f, 0.0f, x2 - x, y2 - y, srcx, srcy, srcx2, srcy2);
        GL.glEnd();
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(-this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glTranslatef(-x, -y, 0.0f);
    }

    public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
        this.drawEmbedded(x, y, x2, y2, srcx, srcy, srcx2, srcy2, null);
    }

    public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
        if (filter != null) {
            filter.bind();
        }
        float mywidth = x2 - x;
        float myheight = y2 - y;
        float texwidth = srcx2 - srcx;
        float texheight = srcy2 - srcy;
        float newTextureOffsetX = srcx / (float)this.width * this.textureWidth + this.textureOffsetX;
        float newTextureOffsetY = srcy / (float)this.height * this.textureHeight + this.textureOffsetY;
        float newTextureWidth = texwidth / (float)this.width * this.textureWidth;
        float newTextureHeight = texheight / (float)this.height * this.textureHeight;
        GL.glTexCoord2f(newTextureOffsetX, newTextureOffsetY);
        GL.glVertex3f(x, y, 0.0f);
        GL.glTexCoord2f(newTextureOffsetX, newTextureOffsetY + newTextureHeight);
        GL.glVertex3f(x, y + myheight, 0.0f);
        GL.glTexCoord2f(newTextureOffsetX + newTextureWidth, newTextureOffsetY + newTextureHeight);
        GL.glVertex3f(x + mywidth, y + myheight, 0.0f);
        GL.glTexCoord2f(newTextureOffsetX + newTextureWidth, newTextureOffsetY);
        GL.glVertex3f(x + mywidth, y, 0.0f);
    }

    public void drawWarped(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        Color.white.bind();
        this.texture.bind();
        GL.glTranslatef(x1, y1, 0.0f);
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glBegin(7);
        this.init();
        GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY);
        GL.glVertex3f(0.0f, 0.0f, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX, this.textureOffsetY + this.textureHeight);
        GL.glVertex3f(x2 - x1, y2 - y1, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY + this.textureHeight);
        GL.glVertex3f(x3 - x1, y3 - y1, 0.0f);
        GL.glTexCoord2f(this.textureOffsetX + this.textureWidth, this.textureOffsetY);
        GL.glVertex3f(x4 - x1, y4 - y1, 0.0f);
        GL.glEnd();
        if (this.angle != 0.0f) {
            GL.glTranslatef(this.centerX, this.centerY, 0.0f);
            GL.glRotatef(-this.angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-this.centerX, -this.centerY, 0.0f);
        }
        GL.glTranslatef(-x1, -y1, 0.0f);
    }

    public int getWidth() {
        this.init();
        return this.width;
    }

    public int getHeight() {
        this.init();
        return this.height;
    }

    public Image copy() {
        this.init();
        return this.getSubImage(0, 0, this.width, this.height);
    }

    public Image getScaledCopy(float scale) {
        this.init();
        return this.getScaledCopy((int)((float)this.width * scale), (int)((float)this.height * scale));
    }

    public Image getScaledCopy(int width, int height) {
        this.init();
        Image image = this.copy();
        image.width = width;
        image.height = height;
        image.centerX = width / 2;
        image.centerY = height / 2;
        image.textureOffsetX *= (float)width / (float)this.width;
        image.textureOffsetY *= (float)height / (float)this.height;
        image.textureWidth *= (float)width / (float)this.width;
        image.textureHeight *= (float)height / (float)this.height;
        return image;
    }

    public void ensureInverted() {
        if (this.textureHeight > 0.0f) {
            this.textureOffsetY += this.textureHeight;
            this.textureHeight = -this.textureHeight;
        }
    }

    public Image getFlippedCopy(boolean flipHorizontal, boolean flipVertical) {
        this.init();
        Image image = this.copy();
        if (flipHorizontal) {
            image.textureOffsetX = this.textureOffsetX + this.textureWidth;
            image.textureWidth = -this.textureWidth;
        }
        if (flipVertical) {
            image.textureOffsetY = this.textureOffsetY + this.textureHeight;
            image.textureHeight = -this.textureHeight;
        }
        return image;
    }

    public void endUse() {
        if (inUse != this) {
            throw new RuntimeException("The sprite sheet is not currently in use");
        }
        inUse = null;
        GL.glEnd();
    }

    public void startUse() {
        if (inUse != null) {
            throw new RuntimeException("Attempt to start use of a sprite sheet before ending use with another - see endUse()");
        }
        inUse = this;
        this.init();
        Color.white.bind();
        this.texture.bind();
        GL.glBegin(7);
    }

    public String toString() {
        this.init();
        return "[Image " + this.ref + " " + this.width + "x" + this.height + "  " + this.textureOffsetX + "," + this.textureOffsetY + "," + this.textureWidth + "," + this.textureHeight + "]";
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.reinit();
    }

    private int translate(byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }

    public Color getColor(int x, int y) {
        if (this.pixelData == null) {
            this.pixelData = this.texture.getTextureData();
        }
        int xo = (int)(this.textureOffsetX * (float)this.texture.getTextureWidth());
        int yo = (int)(this.textureOffsetY * (float)this.texture.getTextureHeight());
        x = this.textureWidth < 0.0f ? xo - x : xo + x;
        y = this.textureHeight < 0.0f ? yo - y : yo + y;
        int offset = (x %= this.texture.getTextureWidth()) + (y %= this.texture.getTextureHeight()) * this.texture.getTextureWidth();
        offset *= this.texture.hasAlpha() ? 4 : 3;
        if (this.texture.hasAlpha()) {
            return new Color(this.translate(this.pixelData[offset]), this.translate(this.pixelData[offset + 1]), this.translate(this.pixelData[offset + 2]), this.translate(this.pixelData[offset + 3]));
        }
        return new Color(this.translate(this.pixelData[offset]), this.translate(this.pixelData[offset + 1]), this.translate(this.pixelData[offset + 2]));
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void destroy() throws SlickException {
        if (this.isDestroyed()) {
            return;
        }
        this.destroyed = true;
        this.texture.release();
        GraphicsFactory.releaseGraphicsForImage(this);
    }

    public void flushPixelData() {
        this.pixelData = null;
    }
}

