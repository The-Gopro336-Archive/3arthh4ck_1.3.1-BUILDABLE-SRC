package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatPopupFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.SwingUtilities;

class FlatPopupFactory$NonFlashingPopup
extends Popup {
    private Popup delegate;
    private Component contents;
    protected Window popupWindow;
    private Color oldPopupWindowBackground;

    FlatPopupFactory$NonFlashingPopup(Popup delegate, Component contents) {
        this.delegate = delegate;
        this.contents = contents;
        this.popupWindow = SwingUtilities.windowForComponent(contents);
        if (this.popupWindow != null) {
            this.oldPopupWindowBackground = this.popupWindow.getBackground();
            this.popupWindow.setBackground(contents.getBackground());
        }
    }

    @Override
    public void show() {
        if (this.delegate != null) {
            Dimension prefSize;
            Container parent;
            FlatPopupFactory.showPopupAndFixLocation(this.delegate, this.popupWindow);
            if (this.contents instanceof JToolTip && this.popupWindow == null && (parent = this.contents.getParent()) instanceof JPanel && !(prefSize = parent.getPreferredSize()).equals(parent.getSize())) {
                Container mediumWeightPanel = SwingUtilities.getAncestorOfClass(Panel.class, parent);
                Container c = mediumWeightPanel != null ? mediumWeightPanel : parent;
                c.setSize(prefSize);
                c.validate();
            }
        }
    }

    @Override
    public void hide() {
        if (this.delegate != null) {
            this.delegate.hide();
            this.delegate = null;
            this.contents = null;
        }
        if (this.popupWindow != null) {
            this.popupWindow.setBackground(this.oldPopupWindowBackground);
            this.popupWindow = null;
        }
    }
}
