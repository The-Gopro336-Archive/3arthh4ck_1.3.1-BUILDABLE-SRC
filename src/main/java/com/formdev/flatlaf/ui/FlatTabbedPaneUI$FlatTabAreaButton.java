package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

class FlatTabbedPaneUI$FlatTabAreaButton
extends FlatArrowButton {
    public FlatTabbedPaneUI$FlatTabAreaButton(int direction) {
        super(direction, FlatTabbedPaneUI.this.arrowType, FlatTabbedPaneUI.this.foreground, FlatTabbedPaneUI.this.disabledForeground, null, FlatTabbedPaneUI.this.buttonHoverBackground, null, FlatTabbedPaneUI.this.buttonPressedBackground);
        this.setArrowWidth(10);
    }

    @Override
    protected Color deriveBackground(Color background) {
        return FlatUIUtils.deriveColor(background, FlatTabbedPaneUI.this.tabPane.getBackground());
    }

    @Override
    public void paint(Graphics g) {
        if (FlatTabbedPaneUI.this.tabsOpaque || FlatTabbedPaneUI.this.tabPane.isOpaque()) {
            g.setColor(FlatTabbedPaneUI.this.tabPane.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        super.paint(g);
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        Insets insets = new Insets(0, 0, 0, 0);
        FlatTabbedPaneUI.rotateInsets(FlatTabbedPaneUI.this.buttonInsets, insets, FlatTabbedPaneUI.this.tabPane.getTabPlacement());
        int top = UIScale.scale2(insets.top);
        int left = UIScale.scale2(insets.left);
        int bottom = UIScale.scale2(insets.bottom);
        int right = UIScale.scale2(insets.right);
        FlatUIUtils.paintComponentBackground(g, left, top, this.getWidth() - left - right, this.getHeight() - top - bottom, 0.0f, UIScale.scale((float)FlatTabbedPaneUI.this.buttonArc));
    }
}
