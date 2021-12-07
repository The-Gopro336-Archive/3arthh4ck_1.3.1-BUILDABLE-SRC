package com.formdev.flatlaf.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class FlatComboBoxUI$1
extends MouseAdapter {
    FlatComboBoxUI$1() {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        FlatComboBoxUI.this.hover = true;
        this.repaintArrowButton();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        FlatComboBoxUI.this.hover = false;
        this.repaintArrowButton();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        FlatComboBoxUI.this.pressed = true;
        this.repaintArrowButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        FlatComboBoxUI.this.pressed = false;
        this.repaintArrowButton();
    }

    private void repaintArrowButton() {
        if (FlatComboBoxUI.this.arrowButton != null && !FlatComboBoxUI.this.comboBox.isEditable()) {
            FlatComboBoxUI.this.arrowButton.repaint();
        }
    }
}
