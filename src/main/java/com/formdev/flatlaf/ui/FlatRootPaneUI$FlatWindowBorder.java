package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;

public class FlatRootPaneUI$FlatWindowBorder
extends BorderUIResource.EmptyBorderUIResource {
    protected final Color activeBorderColor = UIManager.getColor("RootPane.activeBorderColor");
    protected final Color inactiveBorderColor = UIManager.getColor("RootPane.inactiveBorderColor");
    protected final Color baseBorderColor = UIManager.getColor("Panel.background");

    public FlatRootPaneUI$FlatWindowBorder() {
        super(1, 1, 1, 1);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        if (this.isWindowMaximized(c) || FlatUIUtils.isFullScreen(c)) {
            insets.right = 0;
            insets.bottom = 0;
            insets.left = 0;
            insets.top = 0;
            return insets;
        }
        return super.getBorderInsets(c, insets);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.isWindowMaximized(c) || FlatUIUtils.isFullScreen(c)) {
            return;
        }
        Container parent = c.getParent();
        boolean active = parent instanceof Window ? ((Window)parent).isActive() : false;
        g.setColor(FlatUIUtils.deriveColor(active ? this.activeBorderColor : this.inactiveBorderColor, this.baseBorderColor));
        HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, width, height, this::paintImpl);
    }

    private void paintImpl(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
        g.drawRect(x, y, width - 1, height - 1);
    }

    protected boolean isWindowMaximized(Component c) {
        Container parent = c.getParent();
        return parent instanceof Frame ? (((Frame)parent).getExtendedState() & 6) != 0 : false;
    }
}
