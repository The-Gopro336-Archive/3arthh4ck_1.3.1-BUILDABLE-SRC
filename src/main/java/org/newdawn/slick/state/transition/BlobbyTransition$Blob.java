package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

class BlobbyTransition$Blob {
    private float x;
    private float y;
    private float growSpeed;
    private float rad;

    public BlobbyTransition$Blob(GameContainer container) {
        this.x = (float)(Math.random() * (double)container.getWidth());
        this.y = (float)(Math.random() * (double)container.getWidth());
        this.growSpeed = (float)(1.0 + Math.random() * 1.0);
    }

    public void update(int delta) {
        this.rad += this.growSpeed * (float)delta * 0.4f;
    }

    public void render(Graphics g) {
        g.fillOval(this.x - this.rad, this.y - this.rad, this.rad * 2.0f, this.rad * 2.0f);
    }
}
