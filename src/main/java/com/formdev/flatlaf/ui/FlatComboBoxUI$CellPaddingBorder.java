package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

class FlatComboBoxUI$CellPaddingBorder
extends AbstractBorder {
    private final Insets padding;
    private JComponent rendererComponent;
    private Border rendererBorder;

    FlatComboBoxUI$CellPaddingBorder(Insets padding) {
        this.padding = padding;
    }

    void install(Component c) {
        if (!(c instanceof JComponent)) {
            return;
        }
        JComponent jc = (JComponent)c;
        Border oldBorder = jc.getBorder();
        if (oldBorder == this) {
            return;
        }
        if (oldBorder instanceof FlatComboBoxUI$CellPaddingBorder) {
            ((FlatComboBoxUI$CellPaddingBorder)oldBorder).uninstall();
        }
        this.uninstall();
        this.rendererComponent = jc;
        this.rendererBorder = jc.getBorder();
        this.rendererComponent.setBorder(this);
    }

    void uninstall() {
        if (this.rendererComponent == null) {
            return;
        }
        if (this.rendererComponent.getBorder() == this) {
            this.rendererComponent.setBorder(this.rendererBorder);
        }
        this.rendererComponent = null;
        this.rendererBorder = null;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets padding = UIScale.scale(this.padding);
        if (this.rendererBorder != null) {
            Insets insideInsets = this.rendererBorder.getBorderInsets(c);
            insets.top = Math.max(padding.top, insideInsets.top);
            insets.left = Math.max(padding.left, insideInsets.left);
            insets.bottom = Math.max(padding.bottom, insideInsets.bottom);
            insets.right = Math.max(padding.right, insideInsets.right);
        } else {
            insets.top = padding.top;
            insets.left = padding.left;
            insets.bottom = padding.bottom;
            insets.right = padding.right;
        }
        return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.rendererBorder != null) {
            this.rendererBorder.paintBorder(c, g, x, y, width, height);
        }
    }
}
