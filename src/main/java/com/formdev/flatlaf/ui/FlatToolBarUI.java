/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class FlatToolBarUI
extends BasicToolBarUI {
    protected boolean focusableButtons;

    public static ComponentUI createUI(JComponent c) {
        return new FlatToolBarUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (!this.focusableButtons) {
            this.setButtonsFocusable(false);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        if (!this.focusableButtons) {
            this.setButtonsFocusable(true);
        }
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.focusableButtons = UIManager.getBoolean("ToolBar.focusableButtons");
    }

    @Override
    protected ContainerListener createToolBarContListener() {
        return new BasicToolBarUI.ToolBarContListener(){

            @Override
            public void componentAdded(ContainerEvent e) {
                Component c;
                super.componentAdded(e);
                if (!FlatToolBarUI.this.focusableButtons && (c = e.getChild()) instanceof AbstractButton) {
                    c.setFocusable(false);
                }
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                Component c;
                super.componentRemoved(e);
                if (!FlatToolBarUI.this.focusableButtons && (c = e.getChild()) instanceof AbstractButton) {
                    c.setFocusable(true);
                }
            }
        };
    }

    protected void setButtonsFocusable(boolean focusable) {
        for (Component c : this.toolBar.getComponents()) {
            if (!(c instanceof AbstractButton)) continue;
            c.setFocusable(focusable);
        }
    }

    @Override
    protected void setBorderToRollover(Component c) {
    }

    @Override
    protected void setBorderToNonRollover(Component c) {
    }

    @Override
    protected void setBorderToNormal(Component c) {
    }

    @Override
    protected void installRolloverBorders(JComponent c) {
    }

    @Override
    protected void installNonRolloverBorders(JComponent c) {
    }

    @Override
    protected void installNormalBorders(JComponent c) {
    }

    @Override
    protected Border createRolloverBorder() {
        return null;
    }

    @Override
    protected Border createNonRolloverBorder() {
        return null;
    }

    @Override
    public void setOrientation(int orientation) {
        Insets margin;
        Insets newMargin;
        if (orientation != this.toolBar.getOrientation() && !(newMargin = new Insets(margin.left, margin.top, margin.right, margin.bottom)).equals(margin = this.toolBar.getMargin())) {
            this.toolBar.setMargin(newMargin);
        }
        super.setOrientation(orientation);
    }
}

