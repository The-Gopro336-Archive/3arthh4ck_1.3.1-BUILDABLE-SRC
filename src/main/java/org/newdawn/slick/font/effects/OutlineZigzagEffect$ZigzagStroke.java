package org.newdawn.slick.font.effects;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;

class OutlineZigzagEffect$ZigzagStroke
implements Stroke {
    private static final float FLATNESS = 1.0f;

    private OutlineZigzagEffect$ZigzagStroke() {
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        GeneralPath result = new GeneralPath();
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
        int phase = 0;
        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
                case 0: {
                    moveX = lastX = points[0];
                    moveY = lastY = points[1];
                    result.moveTo(moveX, moveY);
                    next = OutlineZigzagEffect.this.wavelength / 2.0f;
                    break;
                }
                case 4: {
                    points[0] = moveX;
                    points[1] = moveY;
                }
                case 1: {
                    thisX = points[0];
                    thisY = points[1];
                    float dx = thisX - lastX;
                    float dy = thisY - lastY;
                    float distance = (float)Math.sqrt(dx * dx + dy * dy);
                    if (distance >= next) {
                        float r = 1.0f / distance;
                        while (distance >= next) {
                            float x = lastX + next * dx * r;
                            float y = lastY + next * dy * r;
                            if (!(phase & true)) {
                                result.lineTo(x + OutlineZigzagEffect.this.amplitude * dy * r, y - OutlineZigzagEffect.this.amplitude * dx * r);
                            } else {
                                result.lineTo(x - OutlineZigzagEffect.this.amplitude * dy * r, y + OutlineZigzagEffect.this.amplitude * dx * r);
                            }
                            next += OutlineZigzagEffect.this.wavelength;
                            ++phase;
                        }
                    }
                    next -= distance;
                    lastX = thisX;
                    lastY = thisY;
                    if (type != 4) break;
                    result.closePath();
                }
            }
            it.next();
        }
        return new BasicStroke(OutlineZigzagEffect.this.getWidth(), 2, OutlineZigzagEffect.this.getJoin()).createStrokedShape(result);
    }
}
