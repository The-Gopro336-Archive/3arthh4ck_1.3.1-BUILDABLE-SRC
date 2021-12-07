package com.formdev.flatlaf.ui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class FlatPopupFactory$DropShadowPopup$1
implements ComponentListener {
    FlatPopupFactory$DropShadowPopup$1() {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        if (DropShadowPopup.this.dropShadowPanel != null) {
            DropShadowPopup.this.dropShadowPanel.setVisible(true);
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        if (DropShadowPopup.this.dropShadowPanel != null) {
            DropShadowPopup.this.dropShadowPanel.setVisible(false);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        DropShadowPopup.this.moveMediumWeightDropShadow();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        DropShadowPopup.this.resizeMediumWeightDropShadow();
    }
}
