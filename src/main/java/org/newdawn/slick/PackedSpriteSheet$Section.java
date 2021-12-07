package org.newdawn.slick;

import java.io.BufferedReader;
import java.io.IOException;

class PackedSpriteSheet$Section {
    public int x;
    public int y;
    public int width;
    public int height;
    public int tilesx;
    public int tilesy;
    public String name;

    public PackedSpriteSheet$Section(BufferedReader reader) throws IOException {
        this.name = reader.readLine().trim();
        this.x = Integer.parseInt(reader.readLine().trim());
        this.y = Integer.parseInt(reader.readLine().trim());
        this.width = Integer.parseInt(reader.readLine().trim());
        this.height = Integer.parseInt(reader.readLine().trim());
        this.tilesx = Integer.parseInt(reader.readLine().trim());
        this.tilesy = Integer.parseInt(reader.readLine().trim());
        reader.readLine().trim();
        reader.readLine().trim();
        this.tilesx = Math.max(1, this.tilesx);
        this.tilesy = Math.max(1, this.tilesy);
    }
}
