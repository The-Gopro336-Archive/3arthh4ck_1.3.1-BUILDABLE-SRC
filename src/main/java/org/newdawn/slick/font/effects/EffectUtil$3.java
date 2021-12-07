package org.newdawn.slick.font.effects;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.newdawn.slick.font.effects.EffectUtil;

final class EffectUtil$3
extends EffectUtil.DefaultValue {
    final float val$currentValue;
    final float val$min;
    final float val$max;
    final String val$description;

    EffectUtil$3(String x0, String x1, float f, float f2, float f3, String string) {
        this.val$currentValue = f;
        this.val$min = f2;
        this.val$max = f3;
        this.val$description = string;
        super(x0, x1);
    }

    @Override
    public void showDialog() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(this.val$currentValue, this.val$min, this.val$max, 0.1f));
        if (this.showValueDialog(spinner, this.val$description)) {
            this.value = String.valueOf(((Double)spinner.getValue()).floatValue());
        }
    }

    @Override
    public Object getObject() {
        return Float.valueOf(this.value);
    }
}
