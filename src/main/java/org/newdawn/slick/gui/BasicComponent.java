package org.newdawn.slick.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public abstract class BasicComponent
extends AbstractComponent {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public BasicComponent(GUIContext container) {
        super(container);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public abstract void renderImpl(GUIContext var1, Graphics var2);

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        this.renderImpl(container, g);
    }

    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
