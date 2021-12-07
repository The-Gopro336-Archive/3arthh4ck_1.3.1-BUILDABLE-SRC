/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg.inkscape;

import java.util.StringTokenizer;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.newdawn.slick.svg.inkscape.InkscapeNonGeometricData;
import org.w3c.dom.Element;

public class Util {
    public static final String INKSCAPE = "http://www.inkscape.org/namespaces/inkscape";
    public static final String SODIPODI = "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd";
    public static final String XLINK = "http://www.w3.org/1999/xlink";

    static NonGeometricData getNonGeometricData(Element element) {
        String meta = Util.getMetaData(element);
        InkscapeNonGeometricData data = new InkscapeNonGeometricData(meta, element);
        data.addAttribute("id", element.getAttribute("id"));
        data.addAttribute("fill", Util.getStyle(element, "fill"));
        data.addAttribute("stroke", Util.getStyle(element, "stroke"));
        data.addAttribute("opacity", Util.getStyle(element, "opacity"));
        data.addAttribute("stroke-dasharray", Util.getStyle(element, "stroke-dasharray"));
        data.addAttribute("stroke-dashoffset", Util.getStyle(element, "stroke-dashoffset"));
        data.addAttribute("stroke-miterlimit", Util.getStyle(element, "stroke-miterlimit"));
        data.addAttribute("stroke-opacity", Util.getStyle(element, "stroke-opacity"));
        data.addAttribute("stroke-width", Util.getStyle(element, "stroke-width"));
        return data;
    }

    static String getMetaData(Element element) {
        String label = element.getAttributeNS(INKSCAPE, "label");
        if (label != null && !label.equals("")) {
            return label;
        }
        return element.getAttribute("id");
    }

    static String getStyle(Element element, String styleName) {
        String value = element.getAttribute(styleName);
        if (value != null && value.length() > 0) {
            return value;
        }
        String style = element.getAttribute("style");
        return Util.extractStyle(style, styleName);
    }

    static String extractStyle(String style, String attribute) {
        if (style == null) {
            return "";
        }
        StringTokenizer tokens = new StringTokenizer(style, ";");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            String key = token.substring(0, token.indexOf(58));
            if (!key.equals(attribute)) continue;
            return token.substring(token.indexOf(58) + 1);
        }
        return "";
    }

    static Transform getTransform(Element element) {
        return Util.getTransform(element, "transform");
    }

    static Transform getTransform(Element element, String attribute) {
        String str = element.getAttribute(attribute);
        if (str == null) {
            return new Transform();
        }
        if (str.equals("")) {
            return new Transform();
        }
        if (str.startsWith("translate")) {
            str = str.substring(0, str.length() - 1);
            str = str.substring("translate(".length());
            StringTokenizer tokens = new StringTokenizer(str, ", ");
            float x = Float.parseFloat(tokens.nextToken());
            float y = Float.parseFloat(tokens.nextToken());
            return Transform.createTranslateTransform(x, y);
        }
        if (str.startsWith("matrix")) {
            float[] pose = new float[6];
            str = str.substring(0, str.length() - 1);
            str = str.substring("matrix(".length());
            StringTokenizer tokens = new StringTokenizer(str, ", ");
            float[] tr = new float[6];
            for (int j = 0; j < tr.length; ++j) {
                tr[j] = Float.parseFloat(tokens.nextToken());
            }
            pose[0] = tr[0];
            pose[1] = tr[2];
            pose[2] = tr[4];
            pose[3] = tr[1];
            pose[4] = tr[3];
            pose[5] = tr[5];
            return new Transform(pose);
        }
        return new Transform();
    }

    static float getFloatAttribute(Element element, String attr) throws ParsingException {
        String cx = element.getAttribute(attr);
        if (cx == null || cx.equals("")) {
            cx = element.getAttributeNS(SODIPODI, attr);
        }
        try {
            return Float.parseFloat(cx);
        }
        catch (NumberFormatException e) {
            throw new ParsingException(element, "Invalid value for: " + attr, (Throwable)e);
        }
    }

    public static String getAsReference(String value) {
        if (value.length() < 2) {
            return "";
        }
        value = value.substring(1, value.length());
        return value;
    }
}

