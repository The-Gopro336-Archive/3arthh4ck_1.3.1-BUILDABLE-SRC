package org.newdawn.slick.font.effects;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.newdawn.slick.font.effects.EffectUtil;

final class EffectUtil$5
extends EffectUtil.DefaultValue {
    final String[][] val$options;
    final String val$currentValue;
    final String val$description;

    EffectUtil$5(String x0, String x1, String[][] stringArray, String string, String string2) {
        this.val$options = stringArray;
        this.val$currentValue = string;
        this.val$description = string2;
        super(x0, x1);
    }

    @Override
    public void showDialog() {
        int selectedIndex = -1;
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        for (int i = 0; i < this.val$options.length; ++i) {
            model.addElement(this.val$options[i][0]);
            if (!this.getValue(i).equals(this.val$currentValue)) continue;
            selectedIndex = i;
        }
        JComboBox comboBox = new JComboBox(model);
        comboBox.setSelectedIndex(selectedIndex);
        if (this.showValueDialog(comboBox, this.val$description)) {
            this.value = this.getValue(comboBox.getSelectedIndex());
        }
    }

    private String getValue(int i) {
        if (this.val$options[i].length == 1) {
            return this.val$options[i][0];
        }
        return this.val$options[i][1];
    }

    @Override
    public String toString() {
        for (int i = 0; i < this.val$options.length; ++i) {
            if (!this.getValue(i).equals(this.value)) continue;
            return this.val$options[i][0].toString();
        }
        return "";
    }

    @Override
    public Object getObject() {
        return this.value;
    }
}
