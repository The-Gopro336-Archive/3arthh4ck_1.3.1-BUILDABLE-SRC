package org.newdawn.slick.font.effects;

import javax.swing.JComponent;
import javax.swing.JSpinner;

class EffectUtil$DefaultValue$1
implements Runnable {
    final JComponent val$component;

    EffectUtil$DefaultValue$1(JComponent jComponent) {
        this.val$component = jComponent;
    }

    @Override
    public void run() {
        JComponent focusComponent = this.val$component;
        if (focusComponent instanceof JSpinner) {
            focusComponent = ((JSpinner.DefaultEditor)((JSpinner)this.val$component).getEditor()).getTextField();
        }
        focusComponent.requestFocusInWindow();
    }
}
