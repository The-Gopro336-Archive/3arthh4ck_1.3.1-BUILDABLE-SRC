/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.svg;

import java.util.Properties;
import org.newdawn.slick.Color;

public class NonGeometricData {
    public static final String ID = "id";
    public static final String FILL = "fill";
    public static final String STROKE = "stroke";
    public static final String OPACITY = "opacity";
    public static final String STROKE_WIDTH = "stroke-width";
    public static final String STROKE_MITERLIMIT = "stroke-miterlimit";
    public static final String STROKE_DASHARRAY = "stroke-dasharray";
    public static final String STROKE_DASHOFFSET = "stroke-dashoffset";
    public static final String STROKE_OPACITY = "stroke-opacity";
    public static final String NONE = "none";
    private String metaData = "";
    private Properties props = new Properties();

    public NonGeometricData(String metaData) {
        this.metaData = metaData;
        this.addAttribute(STROKE_WIDTH, "1");
    }

    private String morphColor(String str) {
        if (str.equals("")) {
            return "#000000";
        }
        if (str.equals("white")) {
            return "#ffffff";
        }
        if (str.equals("black")) {
            return "#000000";
        }
        return str;
    }

    public void addAttribute(String attribute, String value) {
        if (value == null) {
            value = "";
        }
        if (attribute.equals(FILL)) {
            value = this.morphColor(value);
        }
        if (attribute.equals(STROKE_OPACITY) && value.equals("0")) {
            this.props.setProperty(STROKE, NONE);
        }
        if (attribute.equals(STROKE_WIDTH)) {
            if (value.equals("")) {
                value = "1";
            }
            if (value.endsWith("px")) {
                value = value.substring(0, value.length() - 2);
            }
        }
        if (attribute.equals(STROKE)) {
            if (NONE.equals(this.props.getProperty(STROKE))) {
                return;
            }
            if ("".equals(this.props.getProperty(STROKE))) {
                return;
            }
            value = this.morphColor(value);
        }
        this.props.setProperty(attribute, value);
    }

    public boolean isColor(String attribute) {
        return this.getAttribute(attribute).startsWith("#");
    }

    public String getMetaData() {
        return this.metaData;
    }

    public String getAttribute(String attribute) {
        return this.props.getProperty(attribute);
    }

    public Color getAsColor(String attribute) {
        if (!this.isColor(attribute)) {
            throw new RuntimeException("Attribute " + attribute + " is not specified as a color:" + this.getAttribute(attribute));
        }
        int col = Integer.parseInt(this.getAttribute(attribute).substring(1), 16);
        return new Color(col);
    }

    public String getAsReference(String attribute) {
        String value = this.getAttribute(attribute);
        if (value.length() < 7) {
            return "";
        }
        value = value.substring(5, value.length() - 1);
        return value;
    }

    public float getAsFloat(String attribute) {
        String value = this.getAttribute(attribute);
        if (value == null) {
            return 0.0f;
        }
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Attribute " + attribute + " is not specified as a float:" + this.getAttribute(attribute));
        }
    }

    public boolean isFilled() {
        return this.isColor(FILL);
    }

    public boolean isStroked() {
        return this.isColor(STROKE) && this.getAsFloat(STROKE_WIDTH) > 0.0f;
    }
}

