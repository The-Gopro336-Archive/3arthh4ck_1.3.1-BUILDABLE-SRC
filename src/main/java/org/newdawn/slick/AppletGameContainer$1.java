package org.newdawn.slick;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppletGameContainer;

class AppletGameContainer$1
extends Thread {
    AppletGameContainer$1() {
    }

    @Override
    public void run() {
        try {
            AppletGameContainer.this.canvas.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            if (Display.isCreated()) {
                Display.destroy();
            }
            AppletGameContainer.this.displayParent.setVisible(false);
            AppletGameContainer.this.add(new AppletGameContainer.ConsolePanel(AppletGameContainer.this, e));
            AppletGameContainer.this.validate();
        }
    }
}
