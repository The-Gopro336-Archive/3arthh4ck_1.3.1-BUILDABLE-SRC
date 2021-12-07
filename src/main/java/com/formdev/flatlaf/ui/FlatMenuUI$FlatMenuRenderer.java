package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatMenuItemRenderer;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

class FlatMenuUI$FlatMenuRenderer
extends FlatMenuItemRenderer {
    protected final Color menuBarUnderlineSelectionBackground;
    protected final Color menuBarUnderlineSelectionColor;
    protected final int menuBarUnderlineSelectionHeight;

    protected FlatMenuUI$FlatMenuRenderer(JMenuItem menuItem, Icon checkIcon, Icon arrowIcon, Font acceleratorFont, String acceleratorDelimiter) {
        super(menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter);
        this.menuBarUnderlineSelectionBackground = FlatUIUtils.getUIColor("MenuBar.underlineSelectionBackground", this.underlineSelectionBackground);
        this.menuBarUnderlineSelectionColor = FlatUIUtils.getUIColor("MenuBar.underlineSelectionColor", this.underlineSelectionColor);
        this.menuBarUnderlineSelectionHeight = FlatUIUtils.getUIInt("MenuBar.underlineSelectionHeight", this.underlineSelectionHeight);
    }

    @Override
    protected void paintBackground(Graphics g, Color selectionBackground) {
        ButtonModel model;
        if (this.isUnderlineSelection() && ((JMenu)this.menuItem).isTopLevelMenu()) {
            selectionBackground = this.menuBarUnderlineSelectionBackground;
        }
        if ((model = this.menuItem.getModel()).isRollover() && !model.isArmed() && !model.isSelected() && model.isEnabled() && ((JMenu)this.menuItem).isTopLevelMenu()) {
            g.setColor(this.deriveBackground(FlatMenuUI.this.hoverBackground));
            g.fillRect(0, 0, this.menuItem.getWidth(), this.menuItem.getHeight());
        } else {
            super.paintBackground(g, selectionBackground);
        }
    }

    @Override
    protected void paintUnderlineSelection(Graphics g, Color underlineSelectionColor, int underlineSelectionHeight) {
        if (((JMenu)this.menuItem).isTopLevelMenu()) {
            underlineSelectionColor = this.menuBarUnderlineSelectionColor;
            underlineSelectionHeight = this.menuBarUnderlineSelectionHeight;
        }
        super.paintUnderlineSelection(g, underlineSelectionColor, underlineSelectionHeight);
    }
}
