/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;

public interface Loader {
    public void loadChildren(Element var1, Transform var2) throws ParsingException;
}

