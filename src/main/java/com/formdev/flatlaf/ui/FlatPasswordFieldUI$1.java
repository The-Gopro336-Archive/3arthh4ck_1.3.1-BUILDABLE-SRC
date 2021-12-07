package com.formdev.flatlaf.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class FlatPasswordFieldUI$1
extends KeyAdapter {
    FlatPasswordFieldUI$1() {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.repaint(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.repaint(e);
    }

    private void repaint(KeyEvent e) {
        if (e.getKeyCode() == 20) {
            e.getComponent().repaint();
            FlatPasswordFieldUI.this.scrollCaretToVisible();
        }
    }
}
