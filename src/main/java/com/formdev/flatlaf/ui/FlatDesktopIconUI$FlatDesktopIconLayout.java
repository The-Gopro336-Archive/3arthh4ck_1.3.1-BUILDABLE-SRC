package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

class FlatDesktopIconUI$FlatDesktopIconLayout
implements LayoutManager {
    private FlatDesktopIconUI$FlatDesktopIconLayout() {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return FlatDesktopIconUI.this.dockIcon.getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return FlatDesktopIconUI.this.dockIcon.getMinimumSize();
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        FlatDesktopIconUI.this.dockIcon.setBounds(insets.left, insets.top, parent.getWidth() - insets.left - insets.right, parent.getHeight() - insets.top - insets.bottom);
        Dimension cSize = UIScale.scale(FlatDesktopIconUI.this.closeSize);
        FlatDesktopIconUI.this.closeButton.setBounds(parent.getWidth() - cSize.width, 0, cSize.width, cSize.height);
    }
}
