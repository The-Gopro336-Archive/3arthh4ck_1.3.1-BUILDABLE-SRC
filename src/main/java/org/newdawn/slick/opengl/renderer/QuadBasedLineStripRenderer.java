package org.newdawn.slick.opengl.renderer;

import org.newdawn.slick.opengl.renderer.DefaultLineStripRenderer;
import org.newdawn.slick.opengl.renderer.LineStripRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

public class QuadBasedLineStripRenderer
implements LineStripRenderer {
    private SGL GL = Renderer.get();
    public static int MAX_POINTS = 10000;
    private boolean antialias;
    private float width = 1.0f;
    private float[] points;
    private float[] colours;
    private int pts;
    private int cpt;
    private DefaultLineStripRenderer def = new DefaultLineStripRenderer();
    private boolean renderHalf;
    private boolean lineCaps = false;

    public QuadBasedLineStripRenderer() {
        this.points = new float[MAX_POINTS * 2];
        this.colours = new float[MAX_POINTS * 4];
    }

    @Override
    public void setLineCaps(boolean caps) {
        this.lineCaps = caps;
    }

    @Override
    public void start() {
        if (this.width == 1.0f) {
            this.def.start();
            return;
        }
        this.pts = 0;
        this.cpt = 0;
        this.GL.flush();
        float[] col = this.GL.getCurrentColor();
        this.color(col[0], col[1], col[2], col[3]);
    }

    @Override
    public void end() {
        if (this.width == 1.0f) {
            this.def.end();
            return;
        }
        this.renderLines(this.points, this.pts);
    }

    @Override
    public void vertex(float x, float y) {
        if (this.width == 1.0f) {
            this.def.vertex(x, y);
            return;
        }
        this.points[this.pts * 2] = x;
        this.points[this.pts * 2 + 1] = y;
        ++this.pts;
        int index = this.pts - 1;
        this.color(this.colours[index * 4], this.colours[index * 4 + 1], this.colours[index * 4 + 2], this.colours[index * 4 + 3]);
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setAntiAlias(boolean antialias) {
        this.def.setAntiAlias(antialias);
        this.antialias = antialias;
    }

    public void renderLines(float[] points, int count) {
        if (this.antialias) {
            this.GL.glEnable(2881);
            this.renderLinesImpl(points, count, this.width + 1.0f);
        }
        this.GL.glDisable(2881);
        this.renderLinesImpl(points, count, this.width);
        if (this.antialias) {
            this.GL.glEnable(2881);
        }
    }

    public void renderLinesImpl(float[] points, int count, float w) {
        float ang;
        float step;
        float width = w / 2.0f;
        float lastx1 = 0.0f;
        float lasty1 = 0.0f;
        float lastx2 = 0.0f;
        float lasty2 = 0.0f;
        this.GL.glBegin(7);
        for (int i = 0; i < count + 1; ++i) {
            int current = i;
            int next = i + 1;
            int prev = i - 1;
            if (prev < 0) {
                prev += count;
            }
            if (next >= count) {
                next -= count;
            }
            if (current >= count) {
                current -= count;
            }
            float x1 = points[current * 2];
            float y1 = points[current * 2 + 1];
            float x2 = points[next * 2];
            float y2 = points[next * 2 + 1];
            float dx = x2 - x1;
            float dy = y2 - y1;
            if (dx == 0.0f && dy == 0.0f) continue;
            float d2 = dx * dx + dy * dy;
            float d = (float)Math.sqrt(d2);
            dx *= width;
            dy *= width;
            float tx = dy /= d;
            float ty = -(dx /= d);
            if (i != 0) {
                this.bindColor(prev);
                this.GL.glVertex3f(lastx1, lasty1, 0.0f);
                this.GL.glVertex3f(lastx2, lasty2, 0.0f);
                this.bindColor(current);
                this.GL.glVertex3f(x1 + tx, y1 + ty, 0.0f);
                this.GL.glVertex3f(x1 - tx, y1 - ty, 0.0f);
            }
            lastx1 = x2 - tx;
            lasty1 = y2 - ty;
            lastx2 = x2 + tx;
            lasty2 = y2 + ty;
            if (i >= count - 1) continue;
            this.bindColor(current);
            this.GL.glVertex3f(x1 + tx, y1 + ty, 0.0f);
            this.GL.glVertex3f(x1 - tx, y1 - ty, 0.0f);
            this.bindColor(next);
            this.GL.glVertex3f(x2 - tx, y2 - ty, 0.0f);
            this.GL.glVertex3f(x2 + tx, y2 + ty, 0.0f);
        }
        this.GL.glEnd();
        float f = step = width <= 12.5f ? 5.0f : 180.0f / (float)Math.ceil((double)width / 2.5);
        if (this.lineCaps) {
            float dx = points[2] - points[0];
            float dy = points[3] - points[1];
            float fang = (float)Math.toDegrees(Math.atan2(dy, dx)) + 90.0f;
            if (dx != 0.0f || dy != 0.0f) {
                this.GL.glBegin(6);
                this.bindColor(0);
                this.GL.glVertex2f(points[0], points[1]);
                int i = 0;
                while ((float)i < 180.0f + step) {
                    ang = (float)Math.toRadians(fang + (float)i);
                    this.GL.glVertex2f(points[0] + (float)(Math.cos(ang) * (double)width), points[1] + (float)(Math.sin(ang) * (double)width));
                    i = (int)((float)i + step);
                }
                this.GL.glEnd();
            }
        }
        if (this.lineCaps) {
            float dx = points[count * 2 - 2] - points[count * 2 - 4];
            float dy = points[count * 2 - 1] - points[count * 2 - 3];
            float fang = (float)Math.toDegrees(Math.atan2(dy, dx)) - 90.0f;
            if (dx != 0.0f || dy != 0.0f) {
                this.GL.glBegin(6);
                this.bindColor(count - 1);
                this.GL.glVertex2f(points[count * 2 - 2], points[count * 2 - 1]);
                int i = 0;
                while ((float)i < 180.0f + step) {
                    ang = (float)Math.toRadians(fang + (float)i);
                    this.GL.glVertex2f(points[count * 2 - 2] + (float)(Math.cos(ang) * (double)width), points[count * 2 - 1] + (float)(Math.sin(ang) * (double)width));
                    i = (int)((float)i + step);
                }
                this.GL.glEnd();
            }
        }
    }

    private void bindColor(int index) {
        if (index < this.cpt) {
            if (this.renderHalf) {
                this.GL.glColor4f(this.colours[index * 4] * 0.5f, this.colours[index * 4 + 1] * 0.5f, this.colours[index * 4 + 2] * 0.5f, this.colours[index * 4 + 3] * 0.5f);
            } else {
                this.GL.glColor4f(this.colours[index * 4], this.colours[index * 4 + 1], this.colours[index * 4 + 2], this.colours[index * 4 + 3]);
            }
        }
    }

    @Override
    public void color(float r, float g, float b, float a) {
        if (this.width == 1.0f) {
            this.def.color(r, g, b, a);
            return;
        }
        this.colours[this.pts * 4] = r;
        this.colours[this.pts * 4 + 1] = g;
        this.colours[this.pts * 4 + 2] = b;
        this.colours[this.pts * 4 + 3] = a;
        ++this.cpt;
    }

    @Override
    public boolean applyGLLineFixes() {
        if (this.width == 1.0f) {
            return this.def.applyGLLineFixes();
        }
        return this.def.applyGLLineFixes();
    }
}
