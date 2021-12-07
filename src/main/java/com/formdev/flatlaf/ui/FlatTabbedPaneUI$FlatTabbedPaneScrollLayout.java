package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

class FlatTabbedPaneUI$FlatTabbedPaneScrollLayout
extends FlatTabbedPaneUI.FlatTabbedPaneLayout
implements LayoutManager {
    private final BasicTabbedPaneUI.TabbedPaneLayout delegate;

    protected FlatTabbedPaneUI$FlatTabbedPaneScrollLayout(BasicTabbedPaneUI.TabbedPaneLayout delegate) {
        super(FlatTabbedPaneUI.this);
        this.delegate = delegate;
    }

    @Override
    public void calculateLayoutInfo() {
        this.delegate.calculateLayoutInfo();
    }

    @Override
    protected Dimension calculateTabAreaSize() {
        Dimension size = super.calculateTabAreaSize();
        if (FlatTabbedPaneUI.this.isHorizontalTabPlacement()) {
            size.width = Math.min(size.width, UIScale.scale(100));
        } else {
            size.height = Math.min(size.height, UIScale.scale(100));
        }
        return size;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if (this.isContentEmpty()) {
            return this.calculateTabAreaSize();
        }
        return this.delegate.preferredLayoutSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if (this.isContentEmpty()) {
            return this.calculateTabAreaSize();
        }
        return this.delegate.minimumLayoutSize(parent);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        this.delegate.addLayoutComponent(name, comp);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        this.delegate.removeLayoutComponent(comp);
    }

    @Override
    public void layoutContainer(Container parent) {
        int ty;
        Point viewPosition;
        FlatTabbedPaneUI.this.runWithOriginalLayoutManager(() -> this.delegate.layoutContainer(parent));
        int tabsPopupPolicy = FlatTabbedPaneUI.this.getTabsPopupPolicy();
        int scrollButtonsPolicy = FlatTabbedPaneUI.this.getScrollButtonsPolicy();
        int scrollButtonsPlacement = FlatTabbedPaneUI.this.getScrollButtonsPlacement();
        boolean useMoreTabsButton = tabsPopupPolicy == 2;
        boolean useScrollButtons = scrollButtonsPolicy == 2 || scrollButtonsPolicy == 3;
        boolean hideDisabledScrollButtons = scrollButtonsPolicy == 3 && scrollButtonsPlacement == 100;
        boolean trailingScrollButtons = scrollButtonsPlacement == 11;
        boolean leftToRight = FlatTabbedPaneUI.this.isLeftToRight();
        if (!leftToRight && FlatTabbedPaneUI.this.isHorizontalTabPlacement()) {
            useMoreTabsButton = true;
            useScrollButtons = false;
        }
        JButton backwardButton = null;
        JButton forwardButton = null;
        for (Component c : FlatTabbedPaneUI.this.tabPane.getComponents()) {
            if (!(c instanceof FlatTabbedPaneUI.FlatScrollableTabButton)) continue;
            int direction = ((FlatTabbedPaneUI.FlatScrollableTabButton)c).getDirection();
            if (direction == 7 || direction == 1) {
                backwardButton = (JButton)c;
                continue;
            }
            if (direction != 3 && direction != 5) continue;
            forwardButton = (JButton)c;
        }
        if (backwardButton == null || forwardButton == null) {
            return;
        }
        Rectangle bounds = FlatTabbedPaneUI.this.tabPane.getBounds();
        Insets insets = FlatTabbedPaneUI.this.tabPane.getInsets();
        int tabPlacement = FlatTabbedPaneUI.this.tabPane.getTabPlacement();
        int tabAreaAlignment = FlatTabbedPaneUI.this.getTabAreaAlignment();
        Insets tabAreaInsets = FlatTabbedPaneUI.this.getRealTabAreaInsets(tabPlacement);
        boolean moreTabsButtonVisible = false;
        boolean backwardButtonVisible = false;
        boolean forwardButtonVisible = false;
        if (tabAreaInsets.left != 0 || tabAreaInsets.top != 0) {
            FlatTabbedPaneUI.this.shiftTabs(-tabAreaInsets.left, -tabAreaInsets.top);
            Component view = FlatTabbedPaneUI.this.tabViewport.getView();
            Dimension viewSize = view.getPreferredSize();
            boolean horizontal = tabPlacement == 1 || tabPlacement == 3;
            view.setPreferredSize(new Dimension(viewSize.width - (horizontal ? tabAreaInsets.left : 0), viewSize.height - (horizontal ? 0 : tabAreaInsets.top)));
        }
        if (tabPlacement == 1 || tabPlacement == 3) {
            int rightWidth;
            int leftWidth;
            int totalTabWidth;
            if (useScrollButtons && hideDisabledScrollButtons) {
                viewPosition = FlatTabbedPaneUI.this.tabViewport.getViewPosition();
                if (viewPosition.x <= backwardButton.getPreferredSize().width) {
                    FlatTabbedPaneUI.this.tabViewport.setViewPosition(new Point(0, viewPosition.y));
                }
            }
            int tabAreaHeight = FlatTabbedPaneUI.this.maxTabHeight > 0 ? FlatTabbedPaneUI.this.maxTabHeight : Math.max(Math.max(FlatTabbedPaneUI.this.getLeadingPreferredHeight(), FlatTabbedPaneUI.this.getTrailingPreferredHeight()), UIScale.scale(FlatClientProperties.clientPropertyInt(FlatTabbedPaneUI.this.tabPane, "JTabbedPane.tabHeight", FlatTabbedPaneUI.this.tabHeight)));
            int tx = insets.left;
            ty = tabPlacement == 1 ? insets.top + tabAreaInsets.top : bounds.height - insets.bottom - tabAreaInsets.bottom - tabAreaHeight;
            int tw = bounds.width - insets.left - insets.right;
            int th = tabAreaHeight;
            int leadingWidth = FlatTabbedPaneUI.this.getLeadingPreferredWidth();
            int trailingWidth = FlatTabbedPaneUI.this.getTrailingPreferredWidth();
            int availWidth = tw - leadingWidth - trailingWidth - tabAreaInsets.left - tabAreaInsets.right;
            int n = totalTabWidth = FlatTabbedPaneUI.this.rects.length > 0 ? FlatTabbedPaneUI.this.rectsTotalWidth(leftToRight) : 0;
            if (totalTabWidth < availWidth && FlatTabbedPaneUI.this.rects.length > 0) {
                int diff = availWidth - totalTabWidth;
                switch (tabAreaAlignment) {
                    case 10: {
                        trailingWidth += diff;
                        break;
                    }
                    case 11: {
                        leadingWidth += diff;
                        break;
                    }
                    case 0: {
                        leadingWidth += diff / 2;
                        trailingWidth += diff - diff / 2;
                        break;
                    }
                    case 100: {
                        FlatTabbedPaneUI.this.stretchTabsWidth(diff, leftToRight);
                        totalTabWidth = FlatTabbedPaneUI.this.rectsTotalWidth(leftToRight);
                    }
                }
            } else if (FlatTabbedPaneUI.this.rects.length == 0) {
                trailingWidth = tw - leadingWidth;
            }
            Container leftComponent = leftToRight ? FlatTabbedPaneUI.this.leadingComponent : FlatTabbedPaneUI.this.trailingComponent;
            int n2 = leftWidth = leftToRight ? leadingWidth : trailingWidth;
            if (leftComponent != null) {
                leftComponent.setBounds(tx, ty, leftWidth, th);
            }
            Container rightComponent = leftToRight ? FlatTabbedPaneUI.this.trailingComponent : FlatTabbedPaneUI.this.leadingComponent;
            int n3 = rightWidth = leftToRight ? trailingWidth : leadingWidth;
            if (rightComponent != null) {
                rightComponent.setBounds(tx + tw - rightWidth, ty, rightWidth, th);
            }
            if (FlatTabbedPaneUI.this.rects.length > 0) {
                int txi = tx + leftWidth + (leftToRight ? tabAreaInsets.left : tabAreaInsets.right);
                int twi = tw - leftWidth - rightWidth - tabAreaInsets.left - tabAreaInsets.right;
                int x = txi;
                int w = twi;
                if (w < totalTabWidth) {
                    int buttonWidth;
                    if (useMoreTabsButton) {
                        buttonWidth = ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).moreTabsButton.getPreferredSize().width;
                        FlatTabbedPaneUI.this.moreTabsButton.setBounds(leftToRight ? x + w - buttonWidth : x, ty, buttonWidth, th);
                        x += leftToRight ? 0 : buttonWidth;
                        w -= buttonWidth;
                        moreTabsButtonVisible = true;
                    }
                    if (useScrollButtons) {
                        if (!hideDisabledScrollButtons || forwardButton.isEnabled()) {
                            buttonWidth = forwardButton.getPreferredSize().width;
                            forwardButton.setBounds(leftToRight ? x + w - buttonWidth : x, ty, buttonWidth, th);
                            x += leftToRight ? 0 : buttonWidth;
                            w -= buttonWidth;
                            forwardButtonVisible = true;
                        }
                        if (!hideDisabledScrollButtons || backwardButton.isEnabled()) {
                            buttonWidth = backwardButton.getPreferredSize().width;
                            if (trailingScrollButtons) {
                                backwardButton.setBounds(leftToRight ? x + w - buttonWidth : x, ty, buttonWidth, th);
                                x += leftToRight ? 0 : buttonWidth;
                            } else {
                                backwardButton.setBounds(leftToRight ? x : x + w - buttonWidth, ty, buttonWidth, th);
                                x += leftToRight ? buttonWidth : 0;
                            }
                            w -= buttonWidth;
                            backwardButtonVisible = true;
                        }
                    }
                }
                FlatTabbedPaneUI.this.tabViewport.setBounds(x, ty, w, th);
                if (!leftToRight) {
                    FlatTabbedPaneUI.this.tabViewport.doLayout();
                    FlatTabbedPaneUI.this.shiftTabs(FlatTabbedPaneUI.this.tabViewport.getView().getWidth() - (((FlatTabbedPaneUI)FlatTabbedPaneUI.this).rects[0].x + ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).rects[0].width), 0);
                }
            }
        } else {
            int totalTabHeight;
            if (useScrollButtons && hideDisabledScrollButtons) {
                viewPosition = FlatTabbedPaneUI.this.tabViewport.getViewPosition();
                if (viewPosition.y <= backwardButton.getPreferredSize().height) {
                    FlatTabbedPaneUI.this.tabViewport.setViewPosition(new Point(viewPosition.x, 0));
                }
            }
            int tabAreaWidth = FlatTabbedPaneUI.this.maxTabWidth > 0 ? FlatTabbedPaneUI.this.maxTabWidth : Math.max(FlatTabbedPaneUI.this.getLeadingPreferredWidth(), FlatTabbedPaneUI.this.getTrailingPreferredWidth());
            int tx = tabPlacement == 2 ? insets.left + tabAreaInsets.left : bounds.width - insets.right - tabAreaInsets.right - tabAreaWidth;
            ty = insets.top;
            int tw = tabAreaWidth;
            int th = bounds.height - insets.top - insets.bottom;
            int topHeight = FlatTabbedPaneUI.this.getLeadingPreferredHeight();
            int bottomHeight = FlatTabbedPaneUI.this.getTrailingPreferredHeight();
            int availHeight = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
            int n = totalTabHeight = FlatTabbedPaneUI.this.rects.length > 0 ? FlatTabbedPaneUI.this.rectsTotalHeight() : 0;
            if (totalTabHeight < availHeight && FlatTabbedPaneUI.this.rects.length > 0) {
                int diff = availHeight - totalTabHeight;
                switch (tabAreaAlignment) {
                    case 10: {
                        bottomHeight += diff;
                        break;
                    }
                    case 11: {
                        topHeight += diff;
                        break;
                    }
                    case 0: {
                        topHeight += diff / 2;
                        bottomHeight += diff - diff / 2;
                        break;
                    }
                    case 100: {
                        FlatTabbedPaneUI.this.stretchTabsHeight(diff);
                        totalTabHeight = FlatTabbedPaneUI.this.rectsTotalHeight();
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
            if (FlatTabbedPaneUI.this.rects.length > 0) {
                int tyi = ty + topHeight + tabAreaInsets.top;
                int thi = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
                int y = tyi;
                int h = thi;
                if (h < totalTabHeight) {
                    int buttonHeight;
                    if (useMoreTabsButton) {
                        buttonHeight = ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).moreTabsButton.getPreferredSize().height;
                        FlatTabbedPaneUI.this.moreTabsButton.setBounds(tx, y + h - buttonHeight, tw, buttonHeight);
                        h -= buttonHeight;
                        moreTabsButtonVisible = true;
                    }
                    if (useScrollButtons) {
                        if (!hideDisabledScrollButtons || forwardButton.isEnabled()) {
                            buttonHeight = forwardButton.getPreferredSize().height;
                            forwardButton.setBounds(tx, y + h - buttonHeight, tw, buttonHeight);
                            h -= buttonHeight;
                            forwardButtonVisible = true;
                        }
                        if (!hideDisabledScrollButtons || backwardButton.isEnabled()) {
                            buttonHeight = backwardButton.getPreferredSize().height;
                            if (trailingScrollButtons) {
                                backwardButton.setBounds(tx, y + h - buttonHeight, tw, buttonHeight);
                            } else {
                                backwardButton.setBounds(tx, y, tw, buttonHeight);
                                y += buttonHeight;
                            }
                            h -= buttonHeight;
                            backwardButtonVisible = true;
                        }
                    }
                }
                FlatTabbedPaneUI.this.tabViewport.setBounds(tx, y, tw, h);
            }
        }
        FlatTabbedPaneUI.this.tabViewport.setVisible(FlatTabbedPaneUI.this.rects.length > 0);
        FlatTabbedPaneUI.this.moreTabsButton.setVisible(moreTabsButtonVisible);
        backwardButton.setVisible(backwardButtonVisible);
        forwardButton.setVisible(forwardButtonVisible);
        FlatTabbedPaneUI.this.scrollBackwardButtonPrefSize = backwardButton.getPreferredSize();
    }

    static BasicTabbedPaneUI.TabbedPaneLayout access$000(FlatTabbedPaneUI$FlatTabbedPaneScrollLayout x0) {
        return x0.delegate;
    }
}
