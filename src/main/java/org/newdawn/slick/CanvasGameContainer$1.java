package org.newdawn.slick;

import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

class CanvasGameContainer$1
implements Runnable {
    CanvasGameContainer$1() {
    }

    @Override
    public void run() {
        try {
            Input.disableControllers();
            try {
                Display.setParent((Canvas)CanvasGameContainer.this);
            }
            catch (LWJGLException e) {
                throw new SlickException("Failed to setParent of canvas", e);
            }
            CanvasGameContainer.this.container.setup();
            CanvasGameContainer.this.scheduleUpdate();
        }
        catch (SlickException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
