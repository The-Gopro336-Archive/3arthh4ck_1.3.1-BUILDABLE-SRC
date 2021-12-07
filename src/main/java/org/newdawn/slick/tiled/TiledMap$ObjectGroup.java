package org.newdawn.slick.tiled;

import java.util.ArrayList;
import java.util.Properties;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class TiledMap$ObjectGroup {
    public int index;
    public String name;
    public ArrayList objects;
    public int width;
    public int height;
    public Properties props;

    public TiledMap$ObjectGroup(Element element) throws SlickException {
        NodeList properties;
        this.name = element.getAttribute("name");
        this.width = Integer.parseInt(element.getAttribute("width"));
        this.height = Integer.parseInt(element.getAttribute("height"));
        this.objects = new ArrayList();
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
        NodeList objectNodes = element.getElementsByTagName("object");
        int i = 0;
        while (i < objectNodes.getLength()) {
            Element objElement = (Element)objectNodes.item(i);
            TiledMap.GroupObject object = new TiledMap.GroupObject(TiledMap.this, objElement);
            object.index = i++;
            this.objects.add(object);
        }
    }
}
