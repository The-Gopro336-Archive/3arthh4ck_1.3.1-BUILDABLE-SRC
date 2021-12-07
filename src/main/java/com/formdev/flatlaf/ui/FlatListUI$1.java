package com.formdev.flatlaf.ui;

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import javax.swing.plaf.basic.BasicListUI;

class FlatListUI$1
extends BasicListUI.FocusHandler {
    FlatListUI$1() {
        super(FlatListUI.this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        super.focusGained(e);
        FlatListUI.this.toggleSelectionColors();
    }

    @Override
    public void focusLost(FocusEvent e) {
        super.focusLost(e);
        EventQueue.invokeLater(() -> FlatListUI.this.toggleSelectionColors());
    }
}
