package com.formdev.flatlaf.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

class FlatTitlePane$3
extends BorderLayout {
    FlatTitlePane$3() {
    }

    @Override
    public void layoutContainer(Container target) {
        Component horizontalGlue;
        JMenuBar menuBar;
        super.layoutContainer(target);
        Insets insets = target.getInsets();
        int width = target.getWidth() - insets.left - insets.right;
        if (FlatTitlePane.this.leftPanel.getWidth() + FlatTitlePane.this.buttonPanel.getWidth() > width) {
            int oldWidth = FlatTitlePane.this.leftPanel.getWidth();
            int newWidth = Math.max(width - FlatTitlePane.this.buttonPanel.getWidth(), 0);
            FlatTitlePane.this.leftPanel.setSize(newWidth, FlatTitlePane.this.leftPanel.getHeight());
            if (!FlatTitlePane.this.getComponentOrientation().isLeftToRight()) {
                FlatTitlePane.this.leftPanel.setLocation(FlatTitlePane.this.leftPanel.getX() + (oldWidth - newWidth), FlatTitlePane.this.leftPanel.getY());
            }
        }
        if (FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar = FlatTitlePane.this.rootPane.getJMenuBar()) && (horizontalGlue = FlatTitlePane.this.findHorizontalGlue(menuBar)) != null) {
            Point glueLocation = SwingUtilities.convertPoint(horizontalGlue, 0, 0, FlatTitlePane.this.titleLabel);
            FlatTitlePane.this.titleLabel.setBounds(FlatTitlePane.this.titleLabel.getX() + glueLocation.x, FlatTitlePane.this.titleLabel.getY(), horizontalGlue.getWidth(), FlatTitlePane.this.titleLabel.getHeight());
        }
    }
}
