/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public interface Game {
    public void init(GameContainer var1) throws SlickException;

    public void update(GameContainer var1, int var2) throws SlickException;

    public void render(GameContainer var1, Graphics var2) throws SlickException;

    public boolean closeRequested();

    public String getTitle();
}

