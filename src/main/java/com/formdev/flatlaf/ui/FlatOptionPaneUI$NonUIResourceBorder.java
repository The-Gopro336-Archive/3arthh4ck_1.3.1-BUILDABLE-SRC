package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

class FlatOptionPaneUI$NonUIResourceBorder
implements Border {
    private final Border delegate;

    FlatOptionPaneUI$NonUIResourceBorder(Border delegate) {
        this.delegate = delegate;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        this.delegate.paintBorder(c, g, x, y, width, height);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return this.delegate.getBorderInsets(c);
    }

    @Override
    public boolean isBorderOpaque() {
        return this.delegate.isBorderOpaque();
    }
}
