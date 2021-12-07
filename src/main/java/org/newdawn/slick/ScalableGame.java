package org.newdawn.slick;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

public class ScalableGame
implements Game {
    private static SGL GL = Renderer.get();
    private float normalWidth;
    private float normalHeight;
    private Game held;
    private boolean maintainAspect;
    private int targetWidth;
    private int targetHeight;
    private GameContainer container;

    public ScalableGame(Game held, int normalWidth, int normalHeight) {
        this(held, normalWidth, normalHeight, false);
    }

    public ScalableGame(Game held, int normalWidth, int normalHeight, boolean maintainAspect) {
        this.held = held;
        this.normalWidth = normalWidth;
        this.normalHeight = normalHeight;
        this.maintainAspect = maintainAspect;
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        this.recalculateScale();
        this.held.init(container);
    }

    public void recalculateScale() throws SlickException {
        this.targetWidth = this.container.getWidth();
        this.targetHeight = this.container.getHeight();
        if (this.maintainAspect) {
            boolean normalIsWide = (double)(this.normalWidth / this.normalHeight) > 1.6;
            boolean containerIsWide = (double)((float)this.targetWidth / (float)this.targetHeight) > 1.6;
            float wScale = (float)this.targetWidth / this.normalWidth;
            float hScale = (float)this.targetHeight / this.normalHeight;
            if (normalIsWide & containerIsWide) {
                float scale = wScale < hScale ? wScale : hScale;
                this.targetWidth = (int)(this.normalWidth * scale);
                this.targetHeight = (int)(this.normalHeight * scale);
            } else if (normalIsWide & !containerIsWide) {
                this.targetWidth = (int)(this.normalWidth * wScale);
                this.targetHeight = (int)(this.normalHeight * wScale);
            } else if (!normalIsWide & containerIsWide) {
                this.targetWidth = (int)(this.normalWidth * hScale);
                this.targetHeight = (int)(this.normalHeight * hScale);
            } else {
                float scale = wScale < hScale ? wScale : hScale;
                this.targetWidth = (int)(this.normalWidth * scale);
                this.targetHeight = (int)(this.normalHeight * scale);
            }
        }
        if (this.held instanceof InputListener) {
            this.container.getInput().addListener((InputListener)((Object)this.held));
        }
        this.container.getInput().setScale(this.normalWidth / (float)this.targetWidth, this.normalHeight / (float)this.targetHeight);
        int yoffset = 0;
        int xoffset = 0;
        if (this.targetHeight < this.container.getHeight()) {
            yoffset = (this.container.getHeight() - this.targetHeight) / 2;
        }
        if (this.targetWidth < this.container.getWidth()) {
            xoffset = (this.container.getWidth() - this.targetWidth) / 2;
        }
        this.container.getInput().setOffset((float)(-xoffset) / ((float)this.targetWidth / this.normalWidth), (float)(-yoffset) / ((float)this.targetHeight / this.normalHeight));
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (this.targetHeight != container.getHeight() || this.targetWidth != container.getWidth()) {
            this.recalculateScale();
        }
        this.held.update(container, delta);
    }

    @Override
    public final void render(GameContainer container, Graphics g) throws SlickException {
        int yoffset = 0;
        int xoffset = 0;
        if (this.targetHeight < container.getHeight()) {
            yoffset = (container.getHeight() - this.targetHeight) / 2;
        }
        if (this.targetWidth < container.getWidth()) {
            xoffset = (container.getWidth() - this.targetWidth) / 2;
        }
        SlickCallable.enterSafeBlock();
        g.setClip(xoffset, yoffset, this.targetWidth, this.targetHeight);
        GL.glTranslatef(xoffset, yoffset, 0.0f);
        g.scale((float)this.targetWidth / this.normalWidth, (float)this.targetHeight / this.normalHeight);
        GL.glPushMatrix();
        this.held.render(container, g);
        GL.glPopMatrix();
        g.clearClip();
        SlickCallable.leaveSafeBlock();
        this.renderOverlay(container, g);
    }

    protected void renderOverlay(GameContainer container, Graphics g) {
    }

    @Override
    public boolean closeRequested() {
        return this.held.closeRequested();
    }

    @Override
    public String getTitle() {
        return this.held.getTitle();
    }
}
