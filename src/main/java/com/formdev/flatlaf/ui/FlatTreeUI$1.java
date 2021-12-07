package com.formdev.flatlaf.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

class FlatTreeUI$1
extends BasicTreeUI.MouseHandler {
    FlatTreeUI$1() {
        super(FlatTreeUI.this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(this.handleWideMouseEvent(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(this.handleWideMouseEvent(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(this.handleWideMouseEvent(e));
    }

    private MouseEvent handleWideMouseEvent(MouseEvent e) {
        if (!FlatTreeUI.this.isWideSelection() || !FlatTreeUI.this.tree.isEnabled() || !SwingUtilities.isLeftMouseButton(e) || e.isConsumed()) {
            return e;
        }
        int x = e.getX();
        int y = e.getY();
        TreePath path = FlatTreeUI.this.getClosestPathForLocation(FlatTreeUI.this.tree, x, y);
        if (path == null || FlatTreeUI.this.isLocationInExpandControl(path, x, y)) {
            return e;
        }
        Rectangle bounds = FlatTreeUI.this.getPathBounds(FlatTreeUI.this.tree, path);
        if (bounds == null || y < bounds.y || y >= bounds.y + bounds.height) {
            return e;
        }
        int newX = Math.max(bounds.x, Math.min(x, bounds.x + bounds.width - 1));
        if (newX == x) {
            return e;
        }
        return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers() | e.getModifiersEx(), newX, e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
    }
}
