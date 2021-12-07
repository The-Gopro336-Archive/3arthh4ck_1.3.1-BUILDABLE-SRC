/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.tests.xml;

import org.newdawn.slick.tests.xml.GameData;
import org.newdawn.slick.tests.xml.ItemContainer;
import org.newdawn.slick.util.xml.ObjectTreeParser;
import org.newdawn.slick.util.xml.SlickXMLException;

public class ObjectParserTest {
    public static void main(String[] argv) throws SlickXMLException {
        ObjectTreeParser parser = new ObjectTreeParser("org.newdawn.slick.tests.xml");
        parser.addElementMapping("Bag", ItemContainer.class);
        GameData parsedData = (GameData)parser.parse("testdata/objxmltest.xml");
        parsedData.dump("");
    }
}

