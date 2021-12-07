package org.newdawn.slick.tests.states;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class TestState2
extends BasicGameState {
    public static final int ID = 2;
    private Font font;
    private Image image;
    private float ang;
    private StateBasedGame game;

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        this.font = new AngelCodeFont("testdata/demo2.fnt", "testdata/demo2_00.tga");
        this.image = new Image("testdata/logo.tga");
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) {
        g.setFont(this.font);
        g.setColor(Color.green);
        g.drawString("This is State 2", 200.0f, 50.0f);
        g.rotate(400.0f, 300.0f, this.ang);
        g.drawImage(this.image, 400 - this.image.getWidth() / 2, 300 - this.image.getHeight() / 2);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) {
        this.ang += (float)delta * 0.1f;
    }

    @Override
    public void keyReleased(int key, char c) {
        if (key == 2) {
            this.game.enterState(1, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        if (key == 4) {
            this.game.enterState(3, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }
}
