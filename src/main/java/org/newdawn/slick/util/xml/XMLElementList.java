/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util.xml;

import java.util.ArrayList;
import java.util.Collection;
import org.newdawn.slick.util.xml.XMLElement;

public class XMLElementList {
    private ArrayList list = new ArrayList();

    public void add(XMLElement element) {
        this.list.add(element);
    }

    public int size() {
        return this.list.size();
    }

    public XMLElement get(int i) {
        return (XMLElement)this.list.get(i);
    }

    public boolean contains(XMLElement element) {
        return this.list.contains(element);
    }

    public void addAllTo(Collection collection) {
        collection.addAll(this.list);
    }
}

