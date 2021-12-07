/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.xml;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.xml.SlickXMLException;
import org.newdawn.slick.util.xml.XMLElement;
import org.w3c.dom.Document;

public class XMLParser {
    private static DocumentBuilderFactory factory;

    public XMLElement parse(String ref) throws SlickException {
        return this.parse(ref, ResourceLoader.getResourceAsStream(ref));
    }

    public XMLElement parse(String name, InputStream in) throws SlickXMLException {
        try {
            if (factory == null) {
                factory = DocumentBuilderFactory.newInstance();
            }
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);
            return new XMLElement(doc.getDocumentElement());
        }
        catch (Exception e) {
            throw new SlickXMLException("Failed to parse document: " + name, e);
        }
    }
}

