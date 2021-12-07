package org.newdawn.slick.font.effects;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.newdawn.slick.font.effects.EffectUtil;

final class EffectUtil$2
extends EffectUtil.DefaultValue {
    final int val$currentValue;
    final String val$description;

    EffectUtil$2(String x0, String x1, int n, String string) {
        this.val$currentValue = n;
        this.val$description = string;
        super(x0, x1);
    }

    @Override
    public void showDialog() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(this.val$currentValue, Short.MIN_VALUE, Short.MAX_VALUE, 1));
        if (this.showValueDialog(spinner, this.val$description)) {
            this.value = String.valueOf(spinner.getValue());
        }
    }

    @Override
    public Object getObject() {
        return Integer.valueOf(this.value);
    }
}
