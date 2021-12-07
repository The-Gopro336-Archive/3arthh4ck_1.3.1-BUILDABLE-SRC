package org.newdawn.slick;

import org.newdawn.slick.SlickException;

class CanvasGameContainer$2
implements Runnable {
    CanvasGameContainer$2() {
    }

    @Override
    public void run() {
        try {
            CanvasGameContainer.this.container.gameLoop();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
        CanvasGameContainer.this.container.checkDimensions();
        CanvasGameContainer.this.scheduleUpdate();
    }
}
