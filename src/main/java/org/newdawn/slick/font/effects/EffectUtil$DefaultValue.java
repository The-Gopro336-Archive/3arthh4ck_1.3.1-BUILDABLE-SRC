package org.newdawn.slick.font.effects;

import java.awt.EventQueue;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.EffectUtil;

abstract class EffectUtil$DefaultValue
implements ConfigurableEffect.Value {
    String value;
    String name;

    public EffectUtil$DefaultValue(String name, String value) {
        this.value = value;
        this.name = name;
    }

    @Override
    public void setString(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        if (this.value == null) {
            return "";
        }
        return this.value.toString();
    }

    public boolean showValueDialog(final JComponent component, String description) {
        EffectUtil.ValueDialog dialog = new EffectUtil.ValueDialog(component, this.name, description);
        dialog.setTitle(this.name);
        dialog.setLocationRelativeTo(null);
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                JComponent focusComponent = component;
                if (focusComponent instanceof JSpinner) {
                    focusComponent = ((JSpinner.DefaultEditor)((JSpinner)component).getEditor()).getTextField();
                }
                focusComponent.requestFocusInWindow();
            }
        });
        dialog.setVisible(true);
        return dialog.okPressed;
    }
}
