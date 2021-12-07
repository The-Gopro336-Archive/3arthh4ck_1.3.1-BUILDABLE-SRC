package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicToolBarUI;

class FlatToolBarUI$1
extends BasicToolBarUI.ToolBarContListener {
    FlatToolBarUI$1() {
        super(FlatToolBarUI.this);
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        Component c;
        super.componentAdded(e);
        if (!FlatToolBarUI.this.focusableButtons && (c = e.getChild()) instanceof AbstractButton) {
            c.setFocusable(false);
        }
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        Component c;
        super.componentRemoved(e);
        if (!FlatToolBarUI.this.focusableButtons && (c = e.getChild()) instanceof AbstractButton) {
            c.setFocusable(true);
        }
    }
}
