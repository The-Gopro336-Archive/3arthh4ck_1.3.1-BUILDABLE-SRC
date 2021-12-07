package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatListCellBorder;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JList;
import javax.swing.SwingUtilities;

public class FlatListCellBorder$Selected
extends FlatListCellBorder {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (!this.showCellFocusIndicator) {
            return;
        }
        JList list = (JList)SwingUtilities.getAncestorOfClass(JList.class, c);
        if (list != null && list.getMinSelectionIndex() == list.getMaxSelectionIndex()) {
            return;
        }
        super.paintBorder(c, g, x, y, width, height);
    }
}
