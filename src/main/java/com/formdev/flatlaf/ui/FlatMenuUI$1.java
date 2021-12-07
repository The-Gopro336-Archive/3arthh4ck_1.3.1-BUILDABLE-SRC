package com.formdev.flatlaf.ui;

import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.plaf.basic.BasicMenuUI;

class FlatMenuUI$1
extends BasicMenuUI.MouseInputHandler {
    FlatMenuUI$1() {
        super(FlatMenuUI.this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        this.rollover(e, true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        this.rollover(e, false);
    }

    private void rollover(MouseEvent e, boolean rollover) {
        JMenu menu = (JMenu)e.getSource();
        if (menu.isTopLevelMenu() && menu.isRolloverEnabled()) {
            menu.getModel().setRollover(rollover);
            menu.repaint();
        }
    }
}
