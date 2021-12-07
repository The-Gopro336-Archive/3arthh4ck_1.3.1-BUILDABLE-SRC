package com.formdev.flatlaf.ui;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class FlatScrollPaneUI$Handler
implements ContainerListener,
FocusListener {
    private FlatScrollPaneUI$Handler() {
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        e.getChild().addFocusListener(this);
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        e.getChild().removeFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        FlatScrollPaneUI.this.scrollpane.repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        FlatScrollPaneUI.this.scrollpane.repaint();
    }
}
