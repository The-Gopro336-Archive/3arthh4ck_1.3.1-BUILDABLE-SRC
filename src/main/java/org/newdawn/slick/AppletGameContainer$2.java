package org.newdawn.slick;

import java.awt.Canvas;

class AppletGameContainer$2
extends Canvas {
    AppletGameContainer$2() {
    }

    @Override
    public final void addNotify() {
        super.addNotify();
        AppletGameContainer.this.startLWJGL();
    }

    @Override
    public final void removeNotify() {
        AppletGameContainer.this.destroyLWJGL();
        super.removeNotify();
    }
}
