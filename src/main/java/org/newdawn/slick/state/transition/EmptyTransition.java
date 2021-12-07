package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

public class EmptyTransition
implements Transition {
    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
    }
}
