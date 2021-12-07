/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.svg.NonGeometricData;
import org.w3c.dom.Element;

public class InkscapeNonGeometricData
extends NonGeometricData {
    private Element element;

    public InkscapeNonGeometricData(String metaData, Element element) {
        super(metaData);
        this.element = element;
    }

    @Override
    public String getAttribute(String attribute) {
        String result = super.getAttribute(attribute);
        if (result == null) {
            result = this.element.getAttribute(attribute);
        }
        return result;
    }

    public Element getElement() {
        return this.element;
    }
}

