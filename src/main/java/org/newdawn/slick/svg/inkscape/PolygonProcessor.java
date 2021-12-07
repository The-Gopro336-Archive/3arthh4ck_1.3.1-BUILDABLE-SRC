package org.newdawn.slick.svg.inkscape;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.newdawn.slick.geom.Polygon;
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

public class PolygonProcessor
implements ElementProcessor {
    private static int processPoly(Polygon poly, Element element, StringTokenizer tokens) throws ParsingException {
        int count = 0;
        ArrayList pts = new ArrayList();
        boolean moved = false;
        boolean closed = false;
        while (tokens.hasMoreTokens()) {
            String nextToken = tokens.nextToken();
            if (nextToken.equals("L")) continue;
            if (nextToken.equals("z")) {
                closed = true;
                break;
            }
            if (nextToken.equals("M")) {
                if (!moved) {
                    moved = true;
                    continue;
                }
                return 0;
            }
            if (nextToken.equals("C")) {
                return 0;
            }
            String tokenX = nextToken;
            String tokenY = tokens.nextToken();
            try {
                float x = Float.parseFloat(tokenX);
                float y = Float.parseFloat(tokenY);
                poly.addPoint(x, y);
                ++count;
            }
            catch (NumberFormatException e) {
                throw new ParsingException(element.getAttribute("id"), "Invalid token in points list", (Throwable)e);
            }
        }
        poly.setClosed(closed);
        return count;
    }

    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) throws ParsingException {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        String points = element.getAttribute("points");
        if (element.getNodeName().equals("path")) {
            points = element.getAttribute("d");
        }
        StringTokenizer tokens = new StringTokenizer(points, ", ");
        Polygon poly = new Polygon();
        int count = PolygonProcessor.processPoly(poly, element, tokens);
        NonGeometricData data = Util.getNonGeometricData(element);
        if (count > 3) {
            Shape shape = poly.transform(transform);
            diagram.addFigure(new Figure(5, shape, data, transform));
        }
    }

    @Override
    public boolean handles(Element element) {
        if (element.getNodeName().equals("polygon")) {
            return true;
        }
        return element.getNodeName().equals("path") && !"arc".equals(element.getAttributeNS("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "type"));
    }
}
