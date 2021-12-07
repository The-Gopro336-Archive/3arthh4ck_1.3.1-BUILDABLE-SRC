/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class PackedSpriteSheet {
    private Image image;
    private String basePath;
    private HashMap sections = new HashMap();
    private int filter = 2;

    public PackedSpriteSheet(String def) throws SlickException {
        this(def, null);
    }

    public PackedSpriteSheet(String def, Color trans) throws SlickException {
        def = def.replace('\\', '/');
        this.basePath = def.substring(0, def.lastIndexOf("/") + 1);
        this.loadDefinition(def, trans);
    }

    public PackedSpriteSheet(String def, int filter) throws SlickException {
        this(def, filter, null);
    }

    public PackedSpriteSheet(String def, int filter, Color trans) throws SlickException {
        this.filter = filter;
        def = def.replace('\\', '/');
        this.basePath = def.substring(0, def.lastIndexOf("/") + 1);
        this.loadDefinition(def, trans);
    }

    public Image getFullImage() {
        return this.image;
    }

    public Image getSprite(String name) {
        Section section = (Section)this.sections.get(name);
        if (section == null) {
            throw new RuntimeException("Unknown sprite from packed sheet: " + name);
        }
        return this.image.getSubImage(section.x, section.y, section.width, section.height);
    }

    public SpriteSheet getSpriteSheet(String name) {
        Image image = this.getSprite(name);
        Section section = (Section)this.sections.get(name);
        return new SpriteSheet(image, section.width / section.tilesx, section.height / section.tilesy);
    }

    private void loadDefinition(String def, Color trans) throws SlickException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(def)));
        try {
            this.image = new Image(this.basePath + reader.readLine(), false, this.filter, trans);
            while (reader.ready() && reader.readLine() != null) {
                Section sect = new Section(reader);
                this.sections.put(sect.name, sect);
                if (reader.readLine() != null) continue;
                break;
            }
        }
        catch (Exception e) {
            Log.error(e);
            throw new SlickException("Failed to process definitions file - invalid format?", e);
        }
    }

    private class Section {
        public int x;
        public int y;
        public int width;
        public int height;
        public int tilesx;
        public int tilesy;
        public String name;

        public Section(BufferedReader reader) throws IOException {
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
}

