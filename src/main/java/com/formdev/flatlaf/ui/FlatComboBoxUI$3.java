package com.formdev.flatlaf.ui;

import java.awt.event.FocusEvent;
import javax.swing.plaf.basic.BasicComboBoxUI;

class FlatComboBoxUI$3
extends BasicComboBoxUI.FocusHandler {
    FlatComboBoxUI$3() {
        super(FlatComboBoxUI.this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        super.focusGained(e);
        if (FlatComboBoxUI.this.comboBox != null && FlatComboBoxUI.this.comboBox.isEditable()) {
            FlatComboBoxUI.this.comboBox.repaint();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        super.focusLost(e);
        if (FlatComboBoxUI.this.comboBox != null && FlatComboBoxUI.this.comboBox.isEditable()) {
            FlatComboBoxUI.this.comboBox.repaint();
        }
    }
}
