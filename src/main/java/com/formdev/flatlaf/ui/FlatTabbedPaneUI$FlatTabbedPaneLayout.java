package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

class FlatTabbedPaneUI$FlatTabbedPaneLayout
extends BasicTabbedPaneUI.TabbedPaneLayout {
    protected FlatTabbedPaneUI$FlatTabbedPaneLayout() {
        super(FlatTabbedPaneUI.this);
    }

    @Override
    protected Dimension calculateSize(boolean minimum) {
        if (this.isContentEmpty()) {
            return this.calculateTabAreaSize();
        }
        return super.calculateSize(minimum);
    }

    protected boolean isContentEmpty() {
        int tabCount = FlatTabbedPaneUI.this.tabPane.getTabCount();
        if (tabCount == 0) {
            return false;
        }
        for (int i = 0; i < tabCount; ++i) {
            Component c = FlatTabbedPaneUI.this.tabPane.getComponentAt(i);
            if (c == null) continue;
            Dimension cs = c.getPreferredSize();
            if (cs.width == 0 && cs.height == 0) continue;
            return false;
        }
        return true;
    }

    protected Dimension calculateTabAreaSize() {
        boolean horizontal = FlatTabbedPaneUI.this.isHorizontalTabPlacement();
        int tabPlacement = FlatTabbedPaneUI.this.tabPane.getTabPlacement();
        FontMetrics metrics = FlatTabbedPaneUI.this.getFontMetrics();
        int fontHeight = metrics.getHeight();
        int width = 0;
        int height = 0;
        int tabCount = FlatTabbedPaneUI.this.tabPane.getTabCount();
        for (int i = 0; i < tabCount; ++i) {
            if (horizontal) {
                width += FlatTabbedPaneUI.this.calculateTabWidth(tabPlacement, i, metrics);
                height = Math.max(height, FlatTabbedPaneUI.this.calculateTabHeight(tabPlacement, i, fontHeight));
                continue;
            }
            width = Math.max(width, FlatTabbedPaneUI.this.calculateTabWidth(tabPlacement, i, metrics));
            height += FlatTabbedPaneUI.this.calculateTabHeight(tabPlacement, i, fontHeight);
        }
        if (horizontal) {
            height += UIScale.scale(FlatTabbedPaneUI.this.contentSeparatorHeight);
        } else {
            width += UIScale.scale(FlatTabbedPaneUI.this.contentSeparatorHeight);
        }
        Insets insets = FlatTabbedPaneUI.this.tabPane.getInsets();
        Insets tabAreaInsets = FlatTabbedPaneUI.this.getTabAreaInsets(tabPlacement);
        return new Dimension(width + insets.left + insets.right + tabAreaInsets.left + tabAreaInsets.right, height + insets.bottom + insets.top + tabAreaInsets.top + tabAreaInsets.bottom);
    }

    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        Rectangle bounds = FlatTabbedPaneUI.this.tabPane.getBounds();
        Insets insets = FlatTabbedPaneUI.this.tabPane.getInsets();
        int tabPlacement = FlatTabbedPaneUI.this.tabPane.getTabPlacement();
        int tabAreaAlignment = FlatTabbedPaneUI.this.getTabAreaAlignment();
        Insets tabAreaInsets = FlatTabbedPaneUI.this.getRealTabAreaInsets(tabPlacement);
        boolean leftToRight = FlatTabbedPaneUI.this.isLeftToRight();
        if (tabPlacement == 1 || tabPlacement == 3) {
            Container rightComponent;
            Container leftComponent;
            if (!leftToRight) {
                FlatTabbedPaneUI.this.shiftTabs(insets.left + tabAreaInsets.right + FlatTabbedPaneUI.this.getTrailingPreferredWidth(), 0);
            }
            int tabAreaHeight = FlatTabbedPaneUI.this.maxTabHeight > 0 ? FlatTabbedPaneUI.this.maxTabHeight : Math.max(Math.max(FlatTabbedPaneUI.this.getLeadingPreferredHeight(), FlatTabbedPaneUI.this.getTrailingPreferredHeight()), UIScale.scale(FlatClientProperties.clientPropertyInt(FlatTabbedPaneUI.this.tabPane, "JTabbedPane.tabHeight", FlatTabbedPaneUI.this.tabHeight)));
            int tx = insets.left;
            int ty = tabPlacement == 1 ? insets.top + tabAreaInsets.top : bounds.height - insets.bottom - tabAreaInsets.bottom - tabAreaHeight;
            int tw = bounds.width - insets.left - insets.right;
            int th = tabAreaHeight;
            int leadingWidth = FlatTabbedPaneUI.this.getLeadingPreferredWidth();
            int trailingWidth = FlatTabbedPaneUI.this.getTrailingPreferredWidth();
            if (FlatTabbedPaneUI.this.runCount == 1 && FlatTabbedPaneUI.this.rects.length > 0) {
                int availWidth = tw - leadingWidth - trailingWidth - tabAreaInsets.left - tabAreaInsets.right;
                int totalTabWidth = FlatTabbedPaneUI.this.rectsTotalWidth(leftToRight);
                int diff = availWidth - totalTabWidth;
                switch (tabAreaAlignment) {
                    case 10: {
                        trailingWidth += diff;
                        break;
                    }
                    case 11: {
                        FlatTabbedPaneUI.this.shiftTabs(leftToRight ? diff : -diff, 0);
                        leadingWidth += diff;
                        break;
                    }
                    case 0: {
                        FlatTabbedPaneUI.this.shiftTabs((leftToRight ? diff : -diff) / 2, 0);
                        leadingWidth += diff / 2;
                        trailingWidth += diff - diff / 2;
                        break;
                    }
                    case 100: {
                        FlatTabbedPaneUI.this.stretchTabsWidth(diff, leftToRight);
                    }
                }
            } else if (FlatTabbedPaneUI.this.rects.length == 0) {
                trailingWidth = tw - leadingWidth;
            }
            Container container = leftComponent = leftToRight ? FlatTabbedPaneUI.this.leadingComponent : FlatTabbedPaneUI.this.trailingComponent;
            if (leftComponent != null) {
                int leftWidth = leftToRight ? leadingWidth : trailingWidth;
                leftComponent.setBounds(tx, ty, leftWidth, th);
            }
            Container container2 = rightComponent = leftToRight ? FlatTabbedPaneUI.this.trailingComponent : FlatTabbedPaneUI.this.leadingComponent;
            if (rightComponent != null) {
                int rightWidth = leftToRight ? trailingWidth : leadingWidth;
                rightComponent.setBounds(tx + tw - rightWidth, ty, rightWidth, th);
            }
        } else {
            int tabAreaWidth = FlatTabbedPaneUI.this.maxTabWidth > 0 ? FlatTabbedPaneUI.this.maxTabWidth : Math.max(FlatTabbedPaneUI.this.getLeadingPreferredWidth(), FlatTabbedPaneUI.this.getTrailingPreferredWidth());
            int tx = tabPlacement == 2 ? insets.left + tabAreaInsets.left : bounds.width - insets.right - tabAreaInsets.right - tabAreaWidth;
            int ty = insets.top;
            int tw = tabAreaWidth;
            int th = bounds.height - insets.top - insets.bottom;
            int topHeight = FlatTabbedPaneUI.this.getLeadingPreferredHeight();
            int bottomHeight = FlatTabbedPaneUI.this.getTrailingPreferredHeight();
            if (FlatTabbedPaneUI.this.runCount == 1 && FlatTabbedPaneUI.this.rects.length > 0) {
                int availHeight = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
                int totalTabHeight = FlatTabbedPaneUI.this.rectsTotalHeight();
                int diff = availHeight - totalTabHeight;
                switch (tabAreaAlignment) {
                    case 10: {
                        bottomHeight += diff;
                        break;
                    }
                    case 11: {
                        FlatTabbedPaneUI.this.shiftTabs(0, diff);
                        topHeight += diff;
                        break;
                    }
                    case 0: {
                        FlatTabbedPaneUI.this.shiftTabs(0, diff / 2);
                        topHeight += diff / 2;
                        bottomHeight += diff - diff / 2;
                        break;
                    }
                    case 100: {
                        FlatTabbedPaneUI.this.stretchTabsHeight(diff);
                    }
                }
            } else if (FlatTabbedPaneUI.this.rects.length == 0) {
                bottomHeight = th - topHeight;
            }
            if (FlatTabbedPaneUI.this.leadingComponent != null) {
                FlatTabbedPaneUI.this.leadingComponent.setBounds(tx, ty, tw, topHeight);
            }
            if (FlatTabbedPaneUI.this.trailingComponent != null) {
                FlatTabbedPaneUI.this.trailingComponent.setBounds(tx, ty + th - bottomHeight, tw, bottomHeight);
            }
        }
    }
}
