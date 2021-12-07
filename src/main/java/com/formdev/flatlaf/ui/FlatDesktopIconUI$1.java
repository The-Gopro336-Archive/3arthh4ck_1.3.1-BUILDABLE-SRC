package com.formdev.flatlaf.ui;

import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.event.MouseInputAdapter;

class FlatDesktopIconUI$1
extends MouseInputAdapter {
    FlatDesktopIconUI$1() {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (FlatDesktopIconUI.this.frame.isIcon() && FlatDesktopIconUI.this.desktopIcon.contains(e.getX(), e.getY())) {
            FlatDesktopIconUI.this.hideTitleTip();
            FlatDesktopIconUI.this.closeButton.setVisible(false);
            try {
                FlatDesktopIconUI.this.frame.setIcon(false);
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        FlatDesktopIconUI.this.showTitleTip();
        if (FlatDesktopIconUI.this.frame.isClosable()) {
            FlatDesktopIconUI.this.closeButton.setVisible(true);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        FlatDesktopIconUI.this.hideTitleTip();
        FlatDesktopIconUI.this.closeButton.setVisible(false);
    }
}
