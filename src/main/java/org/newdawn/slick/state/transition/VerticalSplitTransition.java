/*
 * Decompiled with CFR 0.150.
 */
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

public class VerticalSplitTransition
implements Transition {
    protected static SGL GL = Renderer.get();
    private GameState prev;
    private float offset;
    private boolean finish;
    private Color background;

    public VerticalSplitTransition() {
    }

    public VerticalSplitTransition(Color background) {
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
        Color c;
        g.translate(0.0f, -this.offset);
        g.setClip(0, (int)(-this.offset), container.getWidth(), container.getHeight() / 2);
        if (this.background != null) {
            c = g.getColor();
            g.setColor(this.background);
            g.fillRect(0.0f, 0.0f, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
        GL.glPushMatrix();
        this.prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();
        g.resetTransform();
        g.translate(0.0f, this.offset);
        g.setClip(0, (int)((float)(container.getHeight() / 2) + this.offset), container.getWidth(), container.getHeight() / 2);
        if (this.background != null) {
            c = g.getColor();
            g.setColor(this.background);
            g.fillRect(0.0f, 0.0f, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
        GL.glPushMatrix();
        this.prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();
        g.translate(0.0f, -this.offset);
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
        this.offset += (float)delta * 1.0f;
        if (this.offset > (float)(container.getHeight() / 2)) {
            this.finish = true;
        }
    }
}

