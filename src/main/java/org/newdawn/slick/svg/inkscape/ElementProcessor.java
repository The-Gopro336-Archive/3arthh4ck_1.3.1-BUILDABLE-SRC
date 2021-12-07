/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;

public interface ElementProcessor {
    public void process(Loader var1, Element var2, Diagram var3, Transform var4) throws ParsingException;

    public boolean handles(Element var1);
}

