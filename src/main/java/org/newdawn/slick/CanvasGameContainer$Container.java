package org.newdawn.slick;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

class CanvasGameContainer$Container
extends AppGameContainer {
    public CanvasGameContainer$Container(Game game, boolean shared) throws SlickException {
        super(game, CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);
        this.width = CanvasGameContainer.this.getWidth();
        this.height = CanvasGameContainer.this.getHeight();
        if (shared) {
            CanvasGameContainer$Container.enableSharedContext();
        }
    }

    @Override
    protected void updateFPS() {
        super.updateFPS();
    }

    @Override
    protected boolean running() {
        return super.running() && CanvasGameContainer.this.isDisplayable();
    }

    @Override
    public int getHeight() {
        return CanvasGameContainer.this.getHeight();
    }

    @Override
    public int getWidth() {
        return CanvasGameContainer.this.getWidth();
    }

    public void checkDimensions() {
        if (this.width != CanvasGameContainer.this.getWidth() || this.height != CanvasGameContainer.this.getHeight()) {
            try {
                this.setDisplayMode(CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);
            }
            catch (SlickException e) {
                Log.error(e);
            }
        }
    }
}
