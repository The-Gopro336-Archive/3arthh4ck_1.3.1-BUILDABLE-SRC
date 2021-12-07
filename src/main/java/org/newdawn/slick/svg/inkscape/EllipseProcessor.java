package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.newdawn.slick.svg.inkscape.ElementProcessor;
import org.newdawn.slick.svg.inkscape.Util;
import org.w3c.dom.Element;

public class EllipseProcessor
implements ElementProcessor {
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) throws ParsingException {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        float x = Util.getFloatAttribute(element, "cx");
        float y = Util.getFloatAttribute(element, "cy");
        float rx = Util.getFloatAttribute(element, "rx");
        float ry = Util.getFloatAttribute(element, "ry");
        Ellipse ellipse = new Ellipse(x, y, rx, ry);
        Shape shape = ellipse.transform(transform);
        NonGeometricData data = Util.getNonGeometricData(element);
        data.addAttribute("cx", "" + x);
        data.addAttribute("cy", "" + y);
        data.addAttribute("rx", "" + rx);
        data.addAttribute("ry", "" + ry);
        diagram.addFigure(new Figure(1, shape, data, transform));
    }

    @Override
    public boolean handles(Element element) {
        if (element.getNodeName().equals("ellipse")) {
            return true;
        }
        return element.getNodeName().equals("path") && "arc".equals(element.getAttributeNS("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "type"));
    }
}
