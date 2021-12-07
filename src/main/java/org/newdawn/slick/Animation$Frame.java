package org.newdawn.slick;

import org.newdawn.slick.Image;

class Animation$Frame {
    public Image image;
    public int duration;
    public int x = -1;
    public int y = -1;

    public Animation$Frame(Image image, int duration) {
        this.image = image;
        this.duration = duration;
    }

    public Animation$Frame(int duration, int x, int y) {
        this.image = Animation.this.spriteSheet.getSubImage(x, y);
        this.duration = duration;
        this.x = x;
        this.y = y;
    }
}
