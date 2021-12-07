package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.plaf.BorderUIResource;

class FlatRootPaneUI$FlatWindowTitleBorder
extends BorderUIResource.EmptyBorderUIResource {
    private final Color borderColor;

    FlatRootPaneUI$FlatWindowTitleBorder(Color borderColor) {
        super(0, 0, 0, 0);
        this.borderColor = borderColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.showBorder(c)) {
            float lineHeight = UIScale.scale(1.0f);
            FlatUIUtils.paintFilledRectangle(g, this.borderColor, x, y, width, lineHeight);
        }
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(this.showBorder(c) ? 1 : 0, 0, 0, 0);
        return insets;
    }

    private boolean showBorder(Component c) {
        Container parent = c.getParent();
        return parent instanceof JFrame && (((JFrame)parent).getJMenuBar() == null || !((JFrame)parent).getJMenuBar().isVisible()) || parent instanceof JDialog && (((JDialog)parent).getJMenuBar() == null || !((JDialog)parent).getJMenuBar().isVisible());
    }
}
