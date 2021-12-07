package org.newdawn.slick.state.transition;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

public class SelectTransition
implements Transition {
    protected static SGL GL = Renderer.get();
    private GameState prev;
    private boolean finish;
    private Color background;
    private float scale1 = 1.0f;
    private float xp1 = 0.0f;
    private float yp1 = 0.0f;
    private float scale2 = 0.4f;
    private float xp2 = 0.0f;
    private float yp2 = 0.0f;
    private boolean init = false;
    private boolean moveBackDone = false;
    private int pause = 300;

    public SelectTransition() {
    }

    public SelectTransition(Color background) {
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
        g.resetTransform();
        if (!this.moveBackDone) {
            g.translate(this.xp1, this.yp1);
            g.scale(this.scale1, this.scale1);
            g.setClip((int)this.xp1, (int)this.yp1, (int)(this.scale1 * (float)container.getWidth()), (int)(this.scale1 * (float)container.getHeight()));
            this.prev.render(container, game, g);
            g.resetTransform();
            g.clearClip();
        }
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        if (this.moveBackDone) {
            g.translate(this.xp1, this.yp1);
            g.scale(this.scale1, this.scale1);
            g.setClip((int)this.xp1, (int)this.yp1, (int)(this.scale1 * (float)container.getWidth()), (int)(this.scale1 * (float)container.getHeight()));
            this.prev.render(container, game, g);
            g.resetTransform();
            g.clearClip();
        }
        g.translate(this.xp2, this.yp2);
        g.scale(this.scale2, this.scale2);
        g.setClip((int)this.xp2, (int)this.yp2, (int)(this.scale2 * (float)container.getWidth()), (int)(this.scale2 * (float)container.getHeight()));
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
        if (!this.init) {
            this.init = true;
            this.xp2 = container.getWidth() / 2 + 50;
            this.yp2 = container.getHeight() / 4;
        }
        if (!this.moveBackDone) {
            if (this.scale1 > 0.4f) {
                this.scale1 -= (float)delta * 0.002f;
                if (this.scale1 <= 0.4f) {
                    this.scale1 = 0.4f;
                }
                this.xp1 += (float)delta * 0.3f;
                if (this.xp1 > 50.0f) {
                    this.xp1 = 50.0f;
                }
                this.yp1 += (float)delta * 0.5f;
                if (this.yp1 > (float)(container.getHeight() / 4)) {
                    this.yp1 = container.getHeight() / 4;
                }
            } else {
                this.moveBackDone = true;
            }
        } else {
            this.pause -= delta;
            if (this.pause > 0) {
                return;
            }
            if (this.scale2 < 1.0f) {
                this.scale2 += (float)delta * 0.002f;
                if (this.scale2 >= 1.0f) {
                    this.scale2 = 1.0f;
                }
                this.xp2 -= (float)delta * 1.5f;
                if (this.xp2 < 0.0f) {
                    this.xp2 = 0.0f;
                }
                this.yp2 -= (float)delta * 0.5f;
                if (this.yp2 < 0.0f) {
                    this.yp2 = 0.0f;
                }
            } else {
                this.finish = true;
            }
        }
    }
}
