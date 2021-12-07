package org.newdawn.slick.font.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.EffectUtil;

public class OutlineEffect
implements ConfigurableEffect {
    private float width = 2.0f;
    private Color color = Color.black;
    private int join = 2;
    private Stroke stroke;

    public OutlineEffect() {
    }

    public OutlineEffect(int width, Color color) {
        this.width = width;
        this.color = color;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        g = (Graphics2D)g.create();
        if (this.stroke != null) {
            g.setStroke(this.stroke);
        } else {
            g.setStroke(this.getStroke());
        }
        g.setColor(this.color);
        g.draw(glyph.getShape());
        g.dispose();
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getJoin() {
        return this.join;
    }

    public Stroke getStroke() {
        if (this.stroke == null) {
            return new BasicStroke(this.width, 2, this.join);
        }
        return this.stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setJoin(int join) {
        this.join = join;
    }

    public String toString() {
        return "Outline";
    }

    @Override
    public List getValues() {
        ArrayList<ConfigurableEffect.Value> values = new ArrayList<ConfigurableEffect.Value>();
        values.add(EffectUtil.colorValue("Color", this.color));
        values.add(EffectUtil.floatValue("Width", this.width, 0.1f, 999.0f, "This setting controls the width of the outline. The glyphs will need padding so the outline doesn't get clipped."));
        values.add(EffectUtil.optionValue("Join", String.valueOf(this.join), new String[][]{{"Bevel", "2"}, {"Miter", "0"}, {"Round", "1"}}, "This setting defines how the corners of the outline are drawn. This is usually only noticeable at large outline widths."));
        return values;
    }

    @Override
    public void setValues(List values) {
        for (ConfigurableEffect.Value value : values) {
            if (value.getName().equals("Color")) {
                this.color = (Color)value.getObject();
                continue;
            }
            if (value.getName().equals("Width")) {
                this.width = ((Float)value.getObject()).floatValue();
                continue;
            }
            if (!value.getName().equals("Join")) continue;
            this.join = Integer.parseInt((String)value.getObject());
        }
    }
}
