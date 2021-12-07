package org.newdawn.slick.tiled;

import java.util.Properties;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class TiledMap$GroupObject {
    public int index;
    public String name;
    public String type;
    public int x;
    public int y;
    public int width;
    public int height;
    private String image;
    public Properties props;

    public TiledMap$GroupObject(Element element) throws SlickException {
        NodeList properties;
        Element propsElement;
        this.name = element.getAttribute("name");
        this.type = element.getAttribute("type");
        this.x = Integer.parseInt(element.getAttribute("x"));
        this.y = Integer.parseInt(element.getAttribute("y"));
        this.width = Integer.parseInt(element.getAttribute("width"));
        this.height = Integer.parseInt(element.getAttribute("height"));
        Element imageElement = (Element)element.getElementsByTagName("image").item(0);
        if (imageElement != null) {
            this.image = imageElement.getAttribute("source");
        }
        if ((propsElement = (Element)element.getElementsByTagName("properties").item(0)) != null && (properties = propsElement.getElementsByTagName("property")) != null) {
            this.props = new Properties();
            for (int p = 0; p < properties.getLength(); ++p) {
                Element propElement = (Element)properties.item(p);
                String name = propElement.getAttribute("name");
                String value = propElement.getAttribute("value");
                this.props.setProperty(name, value);
            }
        }
    }

    static String access$000(TiledMap$GroupObject x0) {
        return x0.image;
    }
}
