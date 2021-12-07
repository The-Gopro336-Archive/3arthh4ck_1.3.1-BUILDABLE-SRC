package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

class FlatTabbedPaneUI$FlatMoreTabsButton
extends FlatTabbedPaneUI.FlatTabAreaButton
implements ActionListener,
PopupMenuListener {
    private boolean popupVisible;

    public FlatTabbedPaneUI$FlatMoreTabsButton() {
        super(FlatTabbedPaneUI.this, 5);
        this.updateDirection();
        this.setToolTipText(FlatTabbedPaneUI.this.moreTabsButtonToolTipText);
        this.addActionListener(this);
    }

    protected void updateDirection() {
        int direction;
        switch (FlatTabbedPaneUI.this.tabPane.getTabPlacement()) {
            default: {
                direction = 5;
                break;
            }
            case 3: {
                direction = 1;
                break;
            }
            case 2: {
                direction = 3;
                break;
            }
            case 4: {
                direction = 7;
            }
        }
        this.setDirection(direction);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        boolean horizontal = this.direction == 5 || this.direction == 1;
        int margin = UIScale.scale(8);
        return new Dimension(size.width + (horizontal ? margin : 0), size.height + (horizontal ? 0 : margin));
    }

    @Override
    public void paint(Graphics g) {
        if (this.direction == 3 || this.direction == 7) {
            int xoffset = Math.max(UIScale.unscale((this.getWidth() - this.getHeight()) / 2) - 4, 0);
            this.setXOffset(this.direction == 3 ? (float)xoffset : (float)(-xoffset));
        } else {
            this.setXOffset(0.0f);
        }
        super.paint(g);
    }

    @Override
    protected boolean isHover() {
        return super.isHover() || this.popupVisible;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (FlatTabbedPaneUI.this.tabViewport == null) {
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(this);
        Rectangle viewRect = FlatTabbedPaneUI.this.tabViewport.getViewRect();
        int lastIndex = -1;
        for (int i = 0; i < FlatTabbedPaneUI.this.rects.length; ++i) {
            if (viewRect.contains(FlatTabbedPaneUI.this.rects[i])) continue;
            if (lastIndex >= 0 && lastIndex + 1 != i) {
                popupMenu.addSeparator();
            }
            lastIndex = i;
            popupMenu.add(this.createTabMenuItem(i));
        }
        int buttonWidth = this.getWidth();
        int buttonHeight = this.getHeight();
        Dimension popupSize = popupMenu.getPreferredSize();
        int x = FlatTabbedPaneUI.this.isLeftToRight() ? buttonWidth - popupSize.width : 0;
        int y = buttonHeight - popupSize.height;
        switch (FlatTabbedPaneUI.this.tabPane.getTabPlacement()) {
            default: {
                y = buttonHeight;
                break;
            }
            case 3: {
                y = -popupSize.height;
                break;
            }
            case 2: {
                x = buttonWidth;
                break;
            }
            case 4: {
                x = -popupSize.width;
            }
        }
        popupMenu.show(this, x, y);
    }

    protected JMenuItem createTabMenuItem(int tabIndex) {
        Color backgroundAt;
        String title = FlatTabbedPaneUI.this.tabPane.getTitleAt(tabIndex);
        if (StringUtils.isEmpty(title)) {
            Component tabComp = FlatTabbedPaneUI.this.tabPane.getTabComponentAt(tabIndex);
            if (tabComp != null) {
                title = this.findTabTitle(tabComp);
            }
            if (StringUtils.isEmpty(title)) {
                title = FlatTabbedPaneUI.this.tabPane.getAccessibleContext().getAccessibleChild(tabIndex).getAccessibleContext().getAccessibleName();
            }
            if (StringUtils.isEmpty(title) && tabComp instanceof Accessible) {
                title = this.findTabTitleInAccessible((Accessible)((Object)tabComp));
            }
            if (StringUtils.isEmpty(title)) {
                title = tabIndex + 1 + ". Tab";
            }
        }
        JMenuItem menuItem = new JMenuItem(title, FlatTabbedPaneUI.this.tabPane.getIconAt(tabIndex));
        menuItem.setDisabledIcon(FlatTabbedPaneUI.this.tabPane.getDisabledIconAt(tabIndex));
        menuItem.setToolTipText(FlatTabbedPaneUI.this.tabPane.getToolTipTextAt(tabIndex));
        Color foregroundAt = FlatTabbedPaneUI.this.tabPane.getForegroundAt(tabIndex);
        if (foregroundAt != FlatTabbedPaneUI.this.tabPane.getForeground()) {
            menuItem.setForeground(foregroundAt);
        }
        if ((backgroundAt = FlatTabbedPaneUI.this.tabPane.getBackgroundAt(tabIndex)) != FlatTabbedPaneUI.this.tabPane.getBackground()) {
            menuItem.setBackground(backgroundAt);
            menuItem.setOpaque(true);
        }
        if (!FlatTabbedPaneUI.this.tabPane.isEnabledAt(tabIndex)) {
            menuItem.setEnabled(false);
        }
        menuItem.addActionListener(e -> this.selectTab(tabIndex));
        return menuItem;
    }

    private String findTabTitle(Component c) {
        String title = null;
        if (c instanceof JLabel) {
            title = ((JLabel)c).getText();
        } else if (c instanceof JTextComponent) {
            title = ((JTextComponent)c).getText();
        }
        if (!StringUtils.isEmpty(title)) {
            return title;
        }
        if (c instanceof Container) {
            for (Component child : ((Container)c).getComponents()) {
                title = this.findTabTitle(child);
                if (title == null) continue;
                return title;
            }
        }
        return null;
    }

    private String findTabTitleInAccessible(Accessible accessible) {
        AccessibleContext context = accessible.getAccessibleContext();
        if (context == null) {
            return null;
        }
        String title = context.getAccessibleName();
        if (!StringUtils.isEmpty(title)) {
            return title;
        }
        int childrenCount = context.getAccessibleChildrenCount();
        for (int i = 0; i < childrenCount; ++i) {
            title = this.findTabTitleInAccessible(context.getAccessibleChild(i));
            if (title == null) continue;
            return title;
        }
        return null;
    }

    protected void selectTab(int tabIndex) {
        FlatTabbedPaneUI.this.tabPane.setSelectedIndex(tabIndex);
        FlatTabbedPaneUI.this.ensureSelectedTabIsVisible();
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        this.popupVisible = true;
        this.repaint();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        this.popupVisible = false;
        this.repaint();
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        this.popupVisible = false;
        this.repaint();
    }
}
