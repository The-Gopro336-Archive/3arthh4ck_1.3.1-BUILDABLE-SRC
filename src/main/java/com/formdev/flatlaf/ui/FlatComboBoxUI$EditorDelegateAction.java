package com.formdev.flatlaf.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

class FlatComboBoxUI$EditorDelegateAction
extends AbstractAction {
    private final KeyStroke keyStroke;

    FlatComboBoxUI$EditorDelegateAction(InputMap inputMap, KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
        inputMap.put(keyStroke, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionListener action = FlatComboBoxUI.this.comboBox.getActionForKeyStroke(this.keyStroke);
        if (action != null) {
            action.actionPerformed(new ActionEvent(FlatComboBoxUI.this.comboBox, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers()));
        }
    }
}
