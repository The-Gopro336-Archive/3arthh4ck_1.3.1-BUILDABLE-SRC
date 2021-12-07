package org.newdawn.slick.svg.inkscape;

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

public class UseProcessor
implements ElementProcessor {
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("use");
    }

    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform transform) throws ParsingException {
        String ref = element.getAttributeNS("http://www.w3.org/1999/xlink", "href");
        String href = Util.getAsReference(ref);
        Figure referenced = diagram.getFigureByID(href);
        if (referenced == null) {
            throw new ParsingException(element, "Unable to locate referenced element: " + href);
        }
        Transform local = Util.getTransform(element);
        Transform trans = local.concatenate(referenced.getTransform());
        NonGeometricData data = Util.getNonGeometricData(element);
        Shape shape = referenced.getShape().transform(trans);
        data.addAttribute("fill", referenced.getData().getAttribute("fill"));
        data.addAttribute("stroke", referenced.getData().getAttribute("stroke"));
        data.addAttribute("opacity", referenced.getData().getAttribute("opacity"));
        data.addAttribute("stroke-width", referenced.getData().getAttribute("stroke-width"));
        Figure figure = new Figure(referenced.getType(), shape, data, trans);
        diagram.addFigure(figure);
    }
}
