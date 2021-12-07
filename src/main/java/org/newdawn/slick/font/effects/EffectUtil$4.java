package org.newdawn.slick.font.effects;

import javax.swing.JCheckBox;
import org.newdawn.slick.font.effects.EffectUtil;

final class EffectUtil$4
extends EffectUtil.DefaultValue {
    final boolean val$currentValue;
    final String val$description;

    EffectUtil$4(String x0, String x1, boolean bl, String string) {
        this.val$currentValue = bl;
        this.val$description = string;
        super(x0, x1);
    }

    @Override
    public void showDialog() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(this.val$currentValue);
        if (this.showValueDialog(checkBox, this.val$description)) {
            this.value = String.valueOf(checkBox.isSelected());
        }
    }

    @Override
    public Object getObject() {
        return Boolean.valueOf(this.value);
    }
}
