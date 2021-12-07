package org.newdawn.slick.font.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.util.List;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.EffectUtil;
import org.newdawn.slick.font.effects.OutlineEffect;

public class OutlineWobbleEffect
extends OutlineEffect {
    private float detail = 1.0f;
    private float amplitude = 1.0f;

    public OutlineWobbleEffect() {
        this.setStroke(new WobbleStroke());
    }

    public float getDetail() {
        return this.detail;
    }

    public void setDetail(float detail) {
        this.detail = detail;
    }

    public float getAmplitude() {
        return this.amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public OutlineWobbleEffect(int width, Color color) {
        super(width, color);
    }

    @Override
    public String toString() {
        return "Outline (Wobble)";
    }

    @Override
    public List getValues() {
        List values = super.getValues();
        values.remove(2);
        values.add(EffectUtil.floatValue("Detail", this.detail, 1.0f, 50.0f, "This setting controls how detailed the outline will be. Smaller numbers cause the outline to have more detail."));
        values.add(EffectUtil.floatValue("Amplitude", this.amplitude, 0.5f, 50.0f, "This setting controls the amplitude of the outline."));
        return values;
    }

    @Override
    public void setValues(List values) {
        super.setValues(values);
        for (ConfigurableEffect.Value value : values) {
            if (value.getName().equals("Detail")) {
                this.detail = ((Float)value.getObject()).floatValue();
                continue;
            }
            if (!value.getName().equals("Amplitude")) continue;
            this.amplitude = ((Float)value.getObject()).floatValue();
        }
    }

    private class WobbleStroke
    implements Stroke {
        private static final float FLATNESS = 1.0f;

        private WobbleStroke() {
        }

        @Override
        public Shape createStrokedShape(Shape shape) {
            GeneralPath result = new GeneralPath();
            shape = new BasicStroke(OutlineWobbleEffect.this.getWidth(), 2, OutlineWobbleEffect.this.getJoin()).createStrokedShape(shape);
            FlatteningPathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), 1.0);
            float[] points = new float[6];
            float moveX = 0.0f;
            float moveY = 0.0f;
            float lastX = 0.0f;
            float lastY = 0.0f;
            float thisX = 0.0f;
            float thisY = 0.0f;
            int type = 0;
            float next = 0.0f;
            while (!it.isDone()) {
                type = it.currentSegment(points);
                switch (type) {
                    case 0: {
                        moveX = lastX = this.randomize(points[0]);
                        moveY = lastY = this.randomize(points[1]);
                        result.moveTo(moveX, moveY);
                        next = 0.0f;
                        break;
                    }
                    case 4: {
                        points[0] = moveX;
                        points[1] = moveY;
                    }
                    case 1: {
                        thisX = this.randomize(points[0]);
                        thisY = this.randomize(points[1]);
                        float dx = thisX - lastX;
                        float dy = thisY - lastY;
                        float distance = (float)Math.sqrt(dx * dx + dy * dy);
                        if (distance >= next) {
                            float r = 1.0f / distance;
                            while (distance >= next) {
                                float x = lastX + next * dx * r;
                                float y = lastY + next * dy * r;
                                result.lineTo(this.randomize(x), this.randomize(y));
                                next += OutlineWobbleEffect.this.detail;
                            }
                        }
                        next -= distance;
                        lastX = thisX;
                        lastY = thisY;
                    }
                }
                it.next();
            }
            return result;
        }

        private float randomize(float x) {
            return x + (float)Math.random() * OutlineWobbleEffect.this.amplitude * 2.0f - 1.0f;
        }
    }
}
