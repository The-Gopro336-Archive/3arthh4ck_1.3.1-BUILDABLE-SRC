/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Layer {
    private static byte[] baseCodes;
    private final TiledMap map;
    public int index;
    public String name;
    public int[][][] data;
    public int width;
    public int height;
    public Properties props;

    public Layer(TiledMap map, Element element) throws SlickException {
        NodeList properties;
        this.map = map;
        this.name = element.getAttribute("name");
        this.width = Integer.parseInt(element.getAttribute("width"));
        this.height = Integer.parseInt(element.getAttribute("height"));
        this.data = new int[this.width][this.height][3];
        Element propsElement = (Element)element.getElementsByTagName("properties").item(0);
        if (propsElement != null && (properties = propsElement.getElementsByTagName("property")) != null) {
            this.props = new Properties();
            for (int p = 0; p < properties.getLength(); ++p) {
                Element propElement = (Element)properties.item(p);
                String name = propElement.getAttribute("name");
                String value = propElement.getAttribute("value");
                this.props.setProperty(name, value);
            }
        }
        Element dataNode = (Element)element.getElementsByTagName("data").item(0);
        String encoding = dataNode.getAttribute("encoding");
        String compression = dataNode.getAttribute("compression");
        if (encoding.equals("base64") && compression.equals("gzip")) {
            try {
                Node cdata = dataNode.getFirstChild();
                char[] enc = cdata.getNodeValue().trim().toCharArray();
                byte[] dec = this.decodeBase64(enc);
                GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(dec));
                for (int y = 0; y < this.height; ++y) {
                    for (int x = 0; x < this.width; ++x) {
                        int tileId = 0;
                        tileId |= is.read();
                        tileId |= is.read() << 8;
                        tileId |= is.read() << 16;
                        if ((tileId |= is.read() << 24) == 0) {
                            this.data[x][y][0] = -1;
                            this.data[x][y][1] = 0;
                            this.data[x][y][2] = 0;
                            continue;
                        }
                        TileSet set = map.findTileSet(tileId);
                        if (set != null) {
                            this.data[x][y][0] = set.index;
                            this.data[x][y][1] = tileId - set.firstGID;
                        }
                        this.data[x][y][2] = tileId;
                    }
                }
            }
            catch (IOException e) {
                Log.error(e);
                throw new SlickException("Unable to decode base 64 block");
            }
        } else {
            throw new SlickException("Unsupport tiled map type: " + encoding + "," + compression + " (only gzip base64 supported)");
        }
    }

    public int getTileID(int x, int y) {
        return this.data[x][y][2];
    }

    public void setTileID(int x, int y, int tile) {
        if (tile == 0) {
            this.data[x][y][0] = -1;
            this.data[x][y][1] = 0;
            this.data[x][y][2] = 0;
        } else {
            TileSet set = this.map.findTileSet(tile);
            this.data[x][y][0] = set.index;
            this.data[x][y][1] = tile - set.firstGID;
            this.data[x][y][2] = tile;
        }
    }

    public void render(int x, int y, int sx, int sy, int width, int ty, boolean lineByLine, int mapTileWidth, int mapTileHeight) {
        for (int tileset = 0; tileset < this.map.getTileSetCount(); ++tileset) {
            TileSet set = null;
            for (int tx = 0; tx < width; ++tx) {
                if (sx + tx < 0 || sy + ty < 0 || sx + tx >= this.width || sy + ty >= this.height || this.data[sx + tx][sy + ty][0] != tileset) continue;
                if (set == null) {
                    set = this.map.getTileSet(tileset);
                    set.tiles.startUse();
                }
                int sheetX = set.getTileX(this.data[sx + tx][sy + ty][1]);
                int sheetY = set.getTileY(this.data[sx + tx][sy + ty][1]);
                int tileOffsetY = set.tileHeight - mapTileHeight;
                set.tiles.renderInUse(x + tx * mapTileWidth, y + ty * mapTileHeight - tileOffsetY, sheetX, sheetY);
            }
            if (lineByLine) {
                if (set != null) {
                    set.tiles.endUse();
                    set = null;
                }
                this.map.renderedLine(ty, ty + sy, this.index);
            }
            if (set == null) continue;
            set.tiles.endUse();
        }
    }

    private byte[] decodeBase64(char[] data) {
        int temp = data.length;
        for (int ix = 0; ix < data.length; ++ix) {
            if (data[ix] <= '\u00ff' && baseCodes[data[ix]] >= 0) continue;
            --temp;
        }
        int len = temp / 4 * 3;
        if (temp % 4 == 3) {
            len += 2;
        }
        if (temp % 4 == 2) {
            ++len;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix = 0; ix < data.length; ++ix) {
            int value;
            int n = value = data[ix] > '\u00ff' ? -1 : baseCodes[data[ix]];
            if (value < 0) continue;
            accum <<= 6;
            accum |= value;
            if ((shift += 6) < 8) continue;
            out[index++] = (byte)(accum >> (shift -= 8) & 0xFF);
        }
        if (index != out.length) {
            throw new RuntimeException("Data length appears to be wrong (wrote " + index + " should be " + out.length + ")");
        }
        return out;
    }

    static {
        int i;
        baseCodes = new byte[256];
        for (i = 0; i < 256; ++i) {
            Layer.baseCodes[i] = -1;
        }
        for (i = 65; i <= 90; ++i) {
            Layer.baseCodes[i] = (byte)(i - 65);
        }
        for (i = 97; i <= 122; ++i) {
            Layer.baseCodes[i] = (byte)(26 + i - 97);
        }
        for (i = 48; i <= 57; ++i) {
            Layer.baseCodes[i] = (byte)(52 + i - 48);
        }
        Layer.baseCodes[43] = 62;
        Layer.baseCodes[47] = 63;
    }
}

