package org.newdawn.slick.font.effects;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.EffectUtil;

public class GradientEffect
implements ConfigurableEffect {
    private Color topColor = Color.cyan;
    private Color bottomColor = Color.blue;
    private int offset = 0;
    private float scale = 1.0f;
    private boolean cyclic;

    public GradientEffect() {
    }

    public GradientEffect(Color topColor, Color bottomColor, float scale) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.scale = scale;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        int ascent = unicodeFont.getAscent();
        float height = (float)ascent * this.scale;
        float top = (float)(-glyph.getYOffset() + unicodeFont.getDescent() + this.offset + ascent / 2) - height / 2.0f;
        g.setPaint(new GradientPaint(0.0f, top, this.topColor, 0.0f, top + height, this.bottomColor, this.cyclic));
        g.fill(glyph.getShape());
    }

    public Color getTopColor() {
        return this.topColor;
    }

    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }

    public Color getBottomColor() {
        return this.bottomColor;
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isCyclic() {
        return this.cyclic;
    }

    public void setCyclic(boolean cyclic) {
        this.cyclic = cyclic;
    }

    public String toString() {
        return "Gradient";
    }

    @Override
    public List getValues() {
        ArrayList<ConfigurableEffect.Value> values = new ArrayList<ConfigurableEffect.Value>();
        values.add(EffectUtil.colorValue("Top color", this.topColor));
        values.add(EffectUtil.colorValue("Bottom color", this.bottomColor));
        values.add(EffectUtil.intValue("Offset", this.offset, "This setting allows you to move the gradient up or down. The gradient is normally centered on the glyph."));
        values.add(EffectUtil.floatValue("Scale", this.scale, 0.0f, 1.0f, "This setting allows you to change the height of the gradient by apercentage. The gradient is normally the height of most glyphs in the font."));
        values.add(EffectUtil.booleanValue("Cyclic", this.cyclic, "If this setting is checked, the gradient will repeat."));
        return values;
    }

    @Override
    public void setValues(List values) {
        for (ConfigurableEffect.Value value : values) {
            if (value.getName().equals("Top color")) {
                this.topColor = (Color)value.getObject();
                continue;
            }
            if (value.getName().equals("Bottom color")) {
                this.bottomColor = (Color)value.getObject();
                continue;
            }
            if (value.getName().equals("Offset")) {
                this.offset = (Integer)value.getObject();
                continue;
            }
            if (value.getName().equals("Scale")) {
                this.scale = ((Float)value.getObject()).floatValue();
                continue;
            }
            if (!value.getName().equals("Cyclic")) continue;
            this.cyclic = (Boolean)value.getObject();
        }
    }
}
