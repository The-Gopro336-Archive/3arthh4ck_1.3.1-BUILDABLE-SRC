package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatInternalFrameTitlePane;
import java.awt.Dimension;
import javax.swing.JPanel;

class FlatInternalFrameTitlePane$1
extends JPanel {
    FlatInternalFrameTitlePane$1() {
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        int height = size.height;
        if (!FlatInternalFrameTitlePane.this.iconButton.isVisible()) {
            height = Math.max(height, ((FlatInternalFrameTitlePane)FlatInternalFrameTitlePane.this).iconButton.getPreferredSize().height);
        }
        if (!FlatInternalFrameTitlePane.this.maxButton.isVisible()) {
            height = Math.max(height, ((FlatInternalFrameTitlePane)FlatInternalFrameTitlePane.this).maxButton.getPreferredSize().height);
        }
        if (!FlatInternalFrameTitlePane.this.closeButton.isVisible()) {
            height = Math.max(height, ((FlatInternalFrameTitlePane)FlatInternalFrameTitlePane.this).closeButton.getPreferredSize().height);
        }
        return new Dimension(size.width, height);
    }
}
