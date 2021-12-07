/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.state.transition;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;
import org.newdawn.slick.util.MaskUtil;

public class BlobbyTransition
implements Transition {
    protected static SGL GL = Renderer.get();
    private GameState prev;
    private boolean finish;
    private Color background;
    private ArrayList blobs = new ArrayList();
    private int timer = 1000;
    private int blobCount = 10;

    public BlobbyTransition() {
    }

    public BlobbyTransition(Color background) {
        this.background = background;
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        this.prev = secondState;
    }

    @Override
    public boolean isComplete() {
        return this.finish;
    }

    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        MaskUtil.resetMask();
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        this.prev.render(container, game, g);
        MaskUtil.defineMask();
        for (int i = 0; i < this.blobs.size(); ++i) {
            ((Blob)this.blobs.get(i)).render(g);
        }
        MaskUtil.finishDefineMask();
        MaskUtil.drawOnMask();
        if (this.background != null) {
            Color c = g.getColor();
            g.setColor(this.background);
            g.fillRect(0.0f, 0.0f, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
        int i;
        if (this.blobs.size() == 0) {
            for (i = 0; i < this.blobCount; ++i) {
                this.blobs.add(new Blob(container));
            }
        }
        for (i = 0; i < this.blobs.size(); ++i) {
            ((Blob)this.blobs.get(i)).update(delta);
        }
        this.timer -= delta;
        if (this.timer < 0) {
            this.finish = true;
        }
    }

    private class Blob {
        private float x;
        private float y;
        private float growSpeed;
        private float rad;

        public Blob(GameContainer container) {
            this.x = (float)(Math.random() * (double)container.getWidth());
            this.y = (float)(Math.random() * (double)container.getWidth());
            this.growSpeed = (float)(1.0 + Math.random() * 1.0);
        }

        public void update(int delta) {
            this.rad += this.growSpeed * (float)delta * 0.4f;
        }

        public void render(Graphics g) {
            g.fillOval(this.x - this.rad, this.y - this.rad, this.rad * 2.0f, this.rad * 2.0f);
        }
    }
}

