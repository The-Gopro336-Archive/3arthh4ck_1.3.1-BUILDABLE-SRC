package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

public abstract class CrossStateTransition
implements Transition {
    private GameState secondState;

    public CrossStateTransition(GameState secondState) {
        this.secondState = secondState;
    }

    @Override
    public abstract boolean isComplete();

    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        this.preRenderSecondState(game, container, g);
        this.secondState.render(container, game, g);
        this.postRenderSecondState(game, container, g);
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        this.preRenderFirstState(game, container, g);
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
    }

    public void preRenderFirstState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }

    public void preRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }

    public void postRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }
}
