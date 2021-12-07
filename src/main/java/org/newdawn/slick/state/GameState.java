/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public interface GameState
extends InputListener {
    public int getID();

    public void init(GameContainer var1, StateBasedGame var2) throws SlickException;

    public void render(GameContainer var1, StateBasedGame var2, Graphics var3) throws SlickException;

    public void update(GameContainer var1, StateBasedGame var2, int var3) throws SlickException;

    public void enter(GameContainer var1, StateBasedGame var2) throws SlickException;

    public void leave(GameContainer var1, StateBasedGame var2) throws SlickException;
}

