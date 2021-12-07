package org.newdawn.slick.font.effects;

import java.awt.Color;
import javax.swing.JColorChooser;
import org.newdawn.slick.font.effects.EffectUtil;

final class EffectUtil$1
extends EffectUtil.DefaultValue {
    EffectUtil$1(String x0, String x1) {
        super(x0, x1);
    }

    @Override
    public void showDialog() {
        Color newColor = JColorChooser.showDialog(null, "Choose a color", EffectUtil.fromString(this.value));
        if (newColor != null) {
            this.value = EffectUtil.toString(newColor);
        }
    }

    @Override
    public Object getObject() {
        return EffectUtil.fromString(this.value);
    }
}
