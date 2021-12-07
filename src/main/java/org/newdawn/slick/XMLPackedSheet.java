/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLPackedSheet {
    private Image image;
    private HashMap sprites = new HashMap();

    public XMLPackedSheet(String imageRef, String xmlRef) throws SlickException {
        this.image = new Image(imageRef, false, 2);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(ResourceLoader.getResourceAsStream(xmlRef));
            NodeList list = doc.getElementsByTagName("sprite");
            for (int i = 0; i < list.getLength(); ++i) {
                Element element = (Element)list.item(i);
                String name = element.getAttribute("name");
                int x = Integer.parseInt(element.getAttribute("x"));
                int y = Integer.parseInt(element.getAttribute("y"));
                int width = Integer.parseInt(element.getAttribute("width"));
                int height = Integer.parseInt(element.getAttribute("height"));
                this.sprites.put(name, this.image.getSubImage(x, y, width, height));
            }
        }
        catch (Exception e) {
            throw new SlickException("Failed to parse sprite sheet XML", e);
        }
    }

    public Image getSprite(String name) {
        return (Image)this.sprites.get(name);
    }
}

