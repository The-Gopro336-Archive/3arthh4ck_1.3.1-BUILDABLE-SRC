package org.newdawn.slick.tiled;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TileSet {
    private final TiledMap map;
    public int index;
    public String name;
    public int firstGID;
    public int lastGID = Integer.MAX_VALUE;
    public int tileWidth;
    public int tileHeight;
    public SpriteSheet tiles;
    public int tilesAcross;
    public int tilesDown;
    private HashMap props = new HashMap();
    protected int tileSpacing = 0;
    protected int tileMargin = 0;

    public TileSet(TiledMap map, Element element, boolean loadImage) throws SlickException {
        String mv;
        this.map = map;
        this.name = element.getAttribute("name");
        this.firstGID = Integer.parseInt(element.getAttribute("firstgid"));
        String source = element.getAttribute("source");
        if (source != null && !source.equals("")) {
            try {
                Element docElement;
                InputStream in = ResourceLoader.getResourceAsStream(map.getTilesLocation() + "/" + source);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(in);
                element = docElement = doc.getDocumentElement();
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Unable to load or parse sourced tileset: " + this.map.tilesLocation + "/" + source);
            }
        }
        String tileWidthString = element.getAttribute("tilewidth");
        String tileHeightString = element.getAttribute("tileheight");
        if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
            throw new SlickException("TiledMap requires that the map be created with tilesets that use a single image.  Check the WiKi for more complete information.");
        }
        this.tileWidth = Integer.parseInt(tileWidthString);
        this.tileHeight = Integer.parseInt(tileHeightString);
        String sv = element.getAttribute("spacing");
        if (sv != null && !sv.equals("")) {
            this.tileSpacing = Integer.parseInt(sv);
        }
        if ((mv = element.getAttribute("margin")) != null && !mv.equals("")) {
            this.tileMargin = Integer.parseInt(mv);
        }
        NodeList list = element.getElementsByTagName("image");
        Element imageNode = (Element)list.item(0);
        String ref = imageNode.getAttribute("source");
        Color trans = null;
        String t = imageNode.getAttribute("trans");
        if (t != null && t.length() > 0) {
            int c = Integer.parseInt(t, 16);
            trans = new Color(c);
        }
        if (loadImage) {
            Image image = new Image(map.getTilesLocation() + "/" + ref, false, 2, trans);
            this.setTileSetImage(image);
        }
        NodeList pElements = element.getElementsByTagName("tile");
        for (int i = 0; i < pElements.getLength(); ++i) {
            Element tileElement = (Element)pElements.item(i);
            int id = Integer.parseInt(tileElement.getAttribute("id"));
            id += this.firstGID;
            Properties tileProps = new Properties();
            Element propsElement = (Element)tileElement.getElementsByTagName("properties").item(0);
            if (propsElement == null) continue;
            NodeList properties = propsElement.getElementsByTagName("property");
            for (int p = 0; p < properties.getLength(); ++p) {
                Element propElement = (Element)properties.item(p);
                String name = propElement.getAttribute("name");
                String value = propElement.getAttribute("value");
                tileProps.setProperty(name, value);
            }
            this.props.put(new Integer(id), tileProps);
        }
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public int getTileSpacing() {
        return this.tileSpacing;
    }

    public int getTileMargin() {
        return this.tileMargin;
    }

    public void setTileSetImage(Image image) {
        this.tiles = new SpriteSheet(image, this.tileWidth, this.tileHeight, this.tileSpacing, this.tileMargin);
        this.tilesAcross = this.tiles.getHorizontalCount();
        this.tilesDown = this.tiles.getVerticalCount();
        if (this.tilesAcross <= 0) {
            this.tilesAcross = 1;
        }
        if (this.tilesDown <= 0) {
            this.tilesDown = 1;
        }
        this.lastGID = this.tilesAcross * this.tilesDown + this.firstGID - 1;
    }

    public Properties getProperties(int globalID) {
        return (Properties)this.props.get(new Integer(globalID));
    }

    public int getTileX(int id) {
        return id % this.tilesAcross;
    }

    public int getTileY(int id) {
        return id / this.tilesAcross;
    }

    public void setLimit(int limit) {
        this.lastGID = limit;
    }

    public boolean contains(int gid) {
        return gid >= this.firstGID && gid <= this.lastGID;
    }
}
