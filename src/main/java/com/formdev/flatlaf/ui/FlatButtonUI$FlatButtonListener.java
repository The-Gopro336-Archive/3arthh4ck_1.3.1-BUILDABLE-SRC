package com.formdev.flatlaf.ui;

import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

class FlatButtonUI$FlatButtonListener
extends BasicButtonListener {
    private final AbstractButton b;

    protected FlatButtonUI$FlatButtonListener(AbstractButton b) {
        super(b);
        this.b = b;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        FlatButtonUI.this.propertyChange(this.b, e);
    }
}
