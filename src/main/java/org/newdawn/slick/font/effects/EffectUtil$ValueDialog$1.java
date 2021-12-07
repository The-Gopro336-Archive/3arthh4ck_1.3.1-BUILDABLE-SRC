package org.newdawn.slick.font.effects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class EffectUtil$ValueDialog$1
implements ActionListener {
    EffectUtil$ValueDialog$1() {
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ValueDialog.this.okPressed = true;
        ValueDialog.this.setVisible(false);
    }
}
