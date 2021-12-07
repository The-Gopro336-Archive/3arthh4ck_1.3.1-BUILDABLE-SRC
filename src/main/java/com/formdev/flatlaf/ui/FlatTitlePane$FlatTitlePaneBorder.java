package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JMenuBar;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

class FlatTitlePane$FlatTitlePaneBorder
extends AbstractBorder {
    protected FlatTitlePane$FlatTitlePaneBorder() {
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        super.getBorderInsets(c, insets);
        Border menuBarBorder = this.getMenuBarBorder();
        if (menuBarBorder != null) {
            Insets menuBarInsets = menuBarBorder.getBorderInsets(c);
            insets.bottom += menuBarInsets.bottom;
        } else if (!(FlatTitlePane.this.borderColor == null || FlatTitlePane.this.rootPane.getJMenuBar() != null && FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
            insets.bottom += UIScale.scale(1);
        }
        if (FlatTitlePane.this.hasNativeCustomDecoration() && !this.isWindowMaximized(c)) {
            insets = FlatUIUtils.addInsets(insets, FlatNativeWindowBorder.WindowTopBorder.getInstance().getBorderInsets());
        }
        return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Border menuBarBorder = this.getMenuBarBorder();
        if (menuBarBorder != null) {
            menuBarBorder.paintBorder(c, g, x, y, width, height);
        } else if (!(FlatTitlePane.this.borderColor == null || FlatTitlePane.this.rootPane.getJMenuBar() != null && FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
            float lineHeight = UIScale.scale(1.0f);
            FlatUIUtils.paintFilledRectangle(g, FlatTitlePane.this.borderColor, x, (float)(y + height) - lineHeight, width, lineHeight);
        }
        if (FlatTitlePane.this.hasNativeCustomDecoration() && !this.isWindowMaximized(c)) {
            FlatNativeWindowBorder.WindowTopBorder.getInstance().paintBorder(c, g, x, y, width, height);
        }
    }

    protected Border getMenuBarBorder() {
        JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
        return FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar) ? menuBar.getBorder() : null;
    }

    protected boolean isWindowMaximized(Component c) {
        return FlatTitlePane.this.window instanceof Frame ? (((Frame)FlatTitlePane.this.window).getExtendedState() & 6) != 0 : false;
    }
}
