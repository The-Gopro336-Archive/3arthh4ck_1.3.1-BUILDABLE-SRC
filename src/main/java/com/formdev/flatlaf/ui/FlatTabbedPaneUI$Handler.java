package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;

class FlatTabbedPaneUI$Handler
implements MouseListener,
MouseMotionListener,
PropertyChangeListener,
ChangeListener,
ComponentListener,
ContainerListener {
    MouseListener mouseDelegate;
    PropertyChangeListener propertyChangeDelegate;
    ChangeListener changeDelegate;
    private final PropertyChangeListener contentListener = this::contentPropertyChange;
    private int pressedTabIndex = -1;
    private int lastTipTabIndex = -1;
    private String lastTip;

    private FlatTabbedPaneUI$Handler() {
    }

    void installListeners() {
        FlatTabbedPaneUI.this.tabPane.addMouseMotionListener(this);
        FlatTabbedPaneUI.this.tabPane.addComponentListener(this);
        FlatTabbedPaneUI.this.tabPane.addContainerListener(this);
        for (Component c : FlatTabbedPaneUI.this.tabPane.getComponents()) {
            if (c instanceof UIResource) continue;
            c.addPropertyChangeListener(this.contentListener);
        }
    }

    void uninstallListeners() {
        FlatTabbedPaneUI.this.tabPane.removeMouseMotionListener(this);
        FlatTabbedPaneUI.this.tabPane.removeComponentListener(this);
        FlatTabbedPaneUI.this.tabPane.removeContainerListener(this);
        for (Component c : FlatTabbedPaneUI.this.tabPane.getComponents()) {
            if (c instanceof UIResource) continue;
            c.removePropertyChangeListener(this.contentListener);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.mouseDelegate.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.updateRollover(e);
        if (!FlatTabbedPaneUI.this.isPressedTabClose()) {
            this.mouseDelegate.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (FlatTabbedPaneUI.this.isPressedTabClose()) {
            this.updateRollover(e);
            if (this.pressedTabIndex >= 0 && this.pressedTabIndex == FlatTabbedPaneUI.this.getRolloverTab()) {
                this.restoreTabToolTip();
                FlatTabbedPaneUI.this.closeTab(this.pressedTabIndex);
            }
        } else {
            this.mouseDelegate.mouseReleased(e);
        }
        this.pressedTabIndex = -1;
        this.updateRollover(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.updateRollover(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.updateRollover(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.updateRollover(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.updateRollover(e);
    }

    private void updateRollover(MouseEvent e) {
        boolean hitClose;
        int x = e.getX();
        int y = e.getY();
        int tabIndex = FlatTabbedPaneUI.this.tabForCoordinate(FlatTabbedPaneUI.this.tabPane, x, y);
        FlatTabbedPaneUI.this.setRolloverTab(tabIndex);
        boolean bl = hitClose = FlatTabbedPaneUI.this.isTabClosable(tabIndex) ? FlatTabbedPaneUI.this.getTabCloseHitArea(tabIndex).contains(x, y) : false;
        if (e.getID() == 501) {
            this.pressedTabIndex = hitClose ? tabIndex : -1;
        }
        FlatTabbedPaneUI.this.setRolloverTabClose(hitClose);
        FlatTabbedPaneUI.this.setPressedTabClose(hitClose && tabIndex == this.pressedTabIndex);
        if (tabIndex >= 0 && hitClose) {
            Object closeTip = FlatTabbedPaneUI.this.getTabClientProperty(tabIndex, "JTabbedPane.tabCloseToolTipText");
            if (closeTip instanceof String) {
                this.setCloseToolTip(tabIndex, (String)closeTip);
            } else {
                this.restoreTabToolTip();
            }
        } else {
            this.restoreTabToolTip();
        }
    }

    private void setCloseToolTip(int tabIndex, String closeTip) {
        if (tabIndex == this.lastTipTabIndex) {
            return;
        }
        if (tabIndex != this.lastTipTabIndex) {
            this.restoreTabToolTip();
        }
        this.lastTipTabIndex = tabIndex;
        this.lastTip = FlatTabbedPaneUI.this.tabPane.getToolTipTextAt(this.lastTipTabIndex);
        FlatTabbedPaneUI.this.tabPane.setToolTipTextAt(this.lastTipTabIndex, closeTip);
    }

    private void restoreTabToolTip() {
        if (this.lastTipTabIndex < 0) {
            return;
        }
        if (this.lastTipTabIndex < FlatTabbedPaneUI.this.tabPane.getTabCount()) {
            FlatTabbedPaneUI.this.tabPane.setToolTipTextAt(this.lastTipTabIndex, this.lastTip);
        }
        this.lastTip = null;
        this.lastTipTabIndex = -1;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "tabPlacement": 
            case "opaque": 
            case "background": 
            case "indexForTabComponent": {
                FlatTabbedPaneUI.this.runWithOriginalLayoutManager(() -> this.propertyChangeDelegate.propertyChange(e));
                break;
            }
            default: {
                this.propertyChangeDelegate.propertyChange(e);
            }
        }
        switch (e.getPropertyName()) {
            case "tabPlacement": {
                if (!(FlatTabbedPaneUI.this.moreTabsButton instanceof FlatTabbedPaneUI.FlatMoreTabsButton)) break;
                ((FlatTabbedPaneUI.FlatMoreTabsButton)FlatTabbedPaneUI.this.moreTabsButton).updateDirection();
                break;
            }
            case "componentOrientation": {
                FlatTabbedPaneUI.this.ensureSelectedTabIsVisibleLater();
                break;
            }
            case "JTabbedPane.showTabSeparators": 
            case "JTabbedPane.showContentSeparator": 
            case "JTabbedPane.hasFullBorder": 
            case "JTabbedPane.hideTabAreaWithOneTab": 
            case "JTabbedPane.minimumTabWidth": 
            case "JTabbedPane.maximumTabWidth": 
            case "JTabbedPane.tabHeight": 
            case "JTabbedPane.tabInsets": 
            case "JTabbedPane.tabAreaInsets": 
            case "JTabbedPane.tabsPopupPolicy": 
            case "JTabbedPane.scrollButtonsPolicy": 
            case "JTabbedPane.scrollButtonsPlacement": 
            case "JTabbedPane.tabAreaAlignment": 
            case "JTabbedPane.tabAlignment": 
            case "JTabbedPane.tabWidthMode": 
            case "JTabbedPane.tabIconPlacement": 
            case "JTabbedPane.tabClosable": {
                FlatTabbedPaneUI.this.tabPane.revalidate();
                FlatTabbedPaneUI.this.tabPane.repaint();
                break;
            }
            case "JTabbedPane.leadingComponent": {
                FlatTabbedPaneUI.this.uninstallLeadingComponent();
                FlatTabbedPaneUI.this.installLeadingComponent();
                FlatTabbedPaneUI.this.tabPane.revalidate();
                FlatTabbedPaneUI.this.tabPane.repaint();
                FlatTabbedPaneUI.this.ensureSelectedTabIsVisibleLater();
                break;
            }
            case "JTabbedPane.trailingComponent": {
                FlatTabbedPaneUI.this.uninstallTrailingComponent();
                FlatTabbedPaneUI.this.installTrailingComponent();
                FlatTabbedPaneUI.this.tabPane.revalidate();
                FlatTabbedPaneUI.this.tabPane.repaint();
                FlatTabbedPaneUI.this.ensureSelectedTabIsVisibleLater();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.changeDelegate.stateChanged(e);
        if (FlatTabbedPaneUI.this.moreTabsButton != null) {
            FlatTabbedPaneUI.this.ensureSelectedTabIsVisible();
        }
    }

    protected void contentPropertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "JTabbedPane.minimumTabWidth": 
            case "JTabbedPane.maximumTabWidth": 
            case "JTabbedPane.tabInsets": 
            case "JTabbedPane.tabAlignment": 
            case "JTabbedPane.tabClosable": {
                FlatTabbedPaneUI.this.tabPane.revalidate();
                FlatTabbedPaneUI.this.tabPane.repaint();
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        FlatTabbedPaneUI.this.ensureSelectedTabIsVisibleLater();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        Component c = e.getChild();
        if (!(c instanceof UIResource)) {
            c.addPropertyChangeListener(this.contentListener);
        }
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        Component c = e.getChild();
        if (!(c instanceof UIResource)) {
            c.removePropertyChangeListener(this.contentListener);
        }
    }
}
