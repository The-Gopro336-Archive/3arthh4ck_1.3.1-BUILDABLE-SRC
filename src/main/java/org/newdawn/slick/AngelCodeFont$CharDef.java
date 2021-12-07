package org.newdawn.slick;

import org.newdawn.slick.Image;

class AngelCodeFont$CharDef {
    public short id;
    public short x;
    public short y;
    public short width;
    public short height;
    public short xoffset;
    public short yoffset;
    public short xadvance;
    public Image image;
    public short dlIndex;
    public short[] kerning;

    private AngelCodeFont$CharDef() {
    }

    public void init() {
        this.image = AngelCodeFont.this.fontImage.getSubImage(this.x, this.y, this.width, this.height);
    }

    public String toString() {
        return "[CharDef id=" + this.id + " x=" + this.x + " y=" + this.y + "]";
    }

    public void draw(float x, float y) {
        this.image.drawEmbedded(x + (float)this.xoffset, y + (float)this.yoffset, this.width, this.height);
    }

    public int getKerning(int otherCodePoint) {
        if (this.kerning == null) {
            return 0;
        }
        int low = 0;
        int high = this.kerning.length - 1;
        while (low <= high) {
            int midIndex = low + high >>> 1;
            short value = this.kerning[midIndex];
            int foundCodePoint = value & 0xFF;
            if (foundCodePoint < otherCodePoint) {
                low = midIndex + 1;
                continue;
            }
            if (foundCodePoint > otherCodePoint) {
                high = midIndex - 1;
                continue;
            }
            return value >> 8;
        }
        return 0;
    }
}
