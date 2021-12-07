package com.formdev.flatlaf.ui;

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import javax.swing.plaf.basic.BasicTableUI;

class FlatTableUI$1
extends BasicTableUI.FocusHandler {
    FlatTableUI$1() {
        super(FlatTableUI.this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        super.focusGained(e);
        FlatTableUI.this.toggleSelectionColors();
    }

    @Override
    public void focusLost(FocusEvent e) {
        super.focusLost(e);
        EventQueue.invokeLater(() -> FlatTableUI.this.toggleSelectionColors());
    }
}
