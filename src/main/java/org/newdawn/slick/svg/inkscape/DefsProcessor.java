/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg.inkscape;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Gradient;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.ParsingException;
import org.newdawn.slick.svg.inkscape.ElementProcessor;
import org.newdawn.slick.svg.inkscape.Util;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefsProcessor
implements ElementProcessor {
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("defs");
    }

    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform transform) throws ParsingException {
        int i;
        NodeList patterns = element.getElementsByTagName("pattern");
        for (int i2 = 0; i2 < patterns.getLength(); ++i2) {
            Element pattern = (Element)patterns.item(i2);
            NodeList list = pattern.getElementsByTagName("image");
            if (list.getLength() == 0) {
                Log.warn("Pattern 1981 does not specify an image. Only image patterns are supported.");
                continue;
            }
            Element image = (Element)list.item(0);
            String patternName = pattern.getAttribute("id");
            String ref = image.getAttributeNS("http://www.w3.org/1999/xlink", "href");
            diagram.addPatternDef(patternName, ref);
        }
        NodeList linear = element.getElementsByTagName("linearGradient");
        ArrayList<Gradient> toResolve = new ArrayList<Gradient>();
        for (int i3 = 0; i3 < linear.getLength(); ++i3) {
            String ref;
            Element lin = (Element)linear.item(i3);
            String name = lin.getAttribute("id");
            Gradient gradient = new Gradient(name, false);
            gradient.setTransform(Util.getTransform(lin, "gradientTransform"));
            if (this.stringLength(lin.getAttribute("x1")) > 0) {
                gradient.setX1(Float.parseFloat(lin.getAttribute("x1")));
            }
            if (this.stringLength(lin.getAttribute("x2")) > 0) {
                gradient.setX2(Float.parseFloat(lin.getAttribute("x2")));
            }
            if (this.stringLength(lin.getAttribute("y1")) > 0) {
                gradient.setY1(Float.parseFloat(lin.getAttribute("y1")));
            }
            if (this.stringLength(lin.getAttribute("y2")) > 0) {
                gradient.setY2(Float.parseFloat(lin.getAttribute("y2")));
            }
            if (this.stringLength(ref = lin.getAttributeNS("http://www.w3.org/1999/xlink", "href")) > 0) {
                gradient.reference(ref.substring(1));
                toResolve.add(gradient);
            } else {
                NodeList steps = lin.getElementsByTagName("stop");
                for (int j = 0; j < steps.getLength(); ++j) {
                    Element s = (Element)steps.item(j);
                    float offset = Float.parseFloat(s.getAttribute("offset"));
                    String colInt = Util.extractStyle(s.getAttribute("style"), "stop-color");
                    String opaInt = Util.extractStyle(s.getAttribute("style"), "stop-opacity");
                    int col = Integer.parseInt(colInt.substring(1), 16);
                    Color stopColor = new Color(col);
                    stopColor.a = Float.parseFloat(opaInt);
                    gradient.addStep(offset, stopColor);
                }
                gradient.getImage();
            }
            diagram.addGradient(name, gradient);
        }
        NodeList radial = element.getElementsByTagName("radialGradient");
        for (i = 0; i < radial.getLength(); ++i) {
            String ref;
            Element rad = (Element)radial.item(i);
            String name = rad.getAttribute("id");
            Gradient gradient = new Gradient(name, true);
            gradient.setTransform(Util.getTransform(rad, "gradientTransform"));
            if (this.stringLength(rad.getAttribute("cx")) > 0) {
                gradient.setX1(Float.parseFloat(rad.getAttribute("cx")));
            }
            if (this.stringLength(rad.getAttribute("cy")) > 0) {
                gradient.setY1(Float.parseFloat(rad.getAttribute("cy")));
            }
            if (this.stringLength(rad.getAttribute("fx")) > 0) {
                gradient.setX2(Float.parseFloat(rad.getAttribute("fx")));
            }
            if (this.stringLength(rad.getAttribute("fy")) > 0) {
                gradient.setY2(Float.parseFloat(rad.getAttribute("fy")));
            }
            if (this.stringLength(rad.getAttribute("r")) > 0) {
                gradient.setR(Float.parseFloat(rad.getAttribute("r")));
            }
            if (this.stringLength(ref = rad.getAttributeNS("http://www.w3.org/1999/xlink", "href")) > 0) {
                gradient.reference(ref.substring(1));
                toResolve.add(gradient);
            } else {
                NodeList steps = rad.getElementsByTagName("stop");
                for (int j = 0; j < steps.getLength(); ++j) {
                    Element s = (Element)steps.item(j);
                    float offset = Float.parseFloat(s.getAttribute("offset"));
                    String colInt = Util.extractStyle(s.getAttribute("style"), "stop-color");
                    String opaInt = Util.extractStyle(s.getAttribute("style"), "stop-opacity");
                    int col = Integer.parseInt(colInt.substring(1), 16);
                    Color stopColor = new Color(col);
                    stopColor.a = Float.parseFloat(opaInt);
                    gradient.addStep(offset, stopColor);
                }
                gradient.getImage();
            }
            diagram.addGradient(name, gradient);
        }
        for (i = 0; i < toResolve.size(); ++i) {
            ((Gradient)toResolve.get(i)).resolve(diagram);
            ((Gradient)toResolve.get(i)).getImage();
        }
    }

    private int stringLength(String value) {
        if (value == null) {
            return 0;
        }
        return value.length();
    }
}

