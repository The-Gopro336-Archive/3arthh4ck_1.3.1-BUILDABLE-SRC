package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.function.Function;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;

class FlatRootPaneUI$FlatRootLayout
implements LayoutManager2 {
    protected FlatRootPaneUI$FlatRootLayout() {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return this.computeLayoutSize(parent, c -> c.getPreferredSize());
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return this.computeLayoutSize(parent, c -> c.getMinimumSize());
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private Dimension computeLayoutSize(Container parent, Function<Component, Dimension> getSizeFunc) {
        JRootPane rootPane = (JRootPane)parent;
        Dimension titlePaneSize = FlatRootPaneUI.this.titlePane != null ? getSizeFunc.apply(FlatRootPaneUI.this.titlePane) : new Dimension();
        Dimension contentSize = rootPane.getContentPane() != null ? getSizeFunc.apply(rootPane.getContentPane()) : rootPane.getSize();
        int width = contentSize.width;
        int height = titlePaneSize.height + contentSize.height;
        if (FlatRootPaneUI.this.titlePane == null || !FlatRootPaneUI.this.titlePane.isMenuBarEmbedded()) {
            JMenuBar menuBar = rootPane.getJMenuBar();
            Dimension menuBarSize = menuBar != null && menuBar.isVisible() ? getSizeFunc.apply(menuBar) : new Dimension();
            width = Math.max(width, menuBarSize.width);
            height += menuBarSize.height;
        }
        Insets insets = rootPane.getInsets();
        return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    @Override
    public void layoutContainer(Container parent) {
        Container contentPane;
        JMenuBar menuBar;
        JRootPane rootPane = (JRootPane)parent;
        boolean isFullScreen = FlatUIUtils.isFullScreen(rootPane);
        Insets insets = rootPane.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = rootPane.getWidth() - insets.left - insets.right;
        int height = rootPane.getHeight() - insets.top - insets.bottom;
        if (rootPane.getLayeredPane() != null) {
            rootPane.getLayeredPane().setBounds(x, y, width, height);
        }
        if (rootPane.getGlassPane() != null) {
            rootPane.getGlassPane().setBounds(x, y, width, height);
        }
        int nextY = 0;
        if (FlatRootPaneUI.this.titlePane != null) {
            int prefHeight = !isFullScreen ? FlatRootPaneUI.this.titlePane.getPreferredSize().height : 0;
            FlatRootPaneUI.this.titlePane.setBounds(0, 0, width, prefHeight);
            nextY += prefHeight;
        }
        if ((menuBar = rootPane.getJMenuBar()) != null && menuBar.isVisible()) {
            boolean embedded;
            boolean bl = embedded = !isFullScreen && FlatRootPaneUI.this.titlePane != null && FlatRootPaneUI.this.titlePane.isMenuBarEmbedded();
            if (embedded) {
                FlatRootPaneUI.this.titlePane.validate();
                menuBar.setBounds(FlatRootPaneUI.this.titlePane.getMenuBarBounds());
            } else {
                Dimension prefSize = menuBar.getPreferredSize();
                menuBar.setBounds(0, nextY, width, prefSize.height);
                nextY += prefSize.height;
            }
        }
        if ((contentPane = rootPane.getContentPane()) != null) {
            contentPane.setBounds(0, nextY, width, Math.max(height - nextY, 0));
        }
        if (FlatRootPaneUI.this.titlePane != null) {
            FlatRootPaneUI.this.titlePane.menuBarLayouted();
        }
    }

    @Override
    public void invalidateLayout(Container parent) {
        if (FlatRootPaneUI.this.titlePane != null) {
            FlatRootPaneUI.this.titlePane.menuBarChanged();
        }
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.0f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.0f;
    }
}
