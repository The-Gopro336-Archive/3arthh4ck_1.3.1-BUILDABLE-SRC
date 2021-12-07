package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;

class FlatComboBoxUI$FlatComboPopup
extends BasicComboPopup {
    protected FlatComboBoxUI$FlatComboPopup(JComboBox combo) {
        super(combo);
        ComponentOrientation o = this.comboBox.getComponentOrientation();
        this.list.setComponentOrientation(o);
        this.scroller.setComponentOrientation(o);
        this.setComponentOrientation(o);
    }

    @Override
    protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
        int displayWidth = FlatComboBoxUI.this.getDisplaySize().width;
        for (Border border : new Border[]{this.scroller.getViewportBorder(), this.scroller.getBorder()}) {
            if (border == null) continue;
            Insets borderInsets = border.getBorderInsets(null);
            displayWidth += borderInsets.left + borderInsets.right;
        }
        JScrollBar verticalScrollBar = this.scroller.getVerticalScrollBar();
        if (verticalScrollBar != null) {
            displayWidth += verticalScrollBar.getPreferredSize().width;
        }
        if (displayWidth > pw) {
            GraphicsConfiguration gc = this.comboBox.getGraphicsConfiguration();
            if (gc != null) {
                Rectangle screenBounds = gc.getBounds();
                Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                displayWidth = Math.min(displayWidth, screenBounds.width - screenInsets.left - screenInsets.right);
            } else {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                displayWidth = Math.min(displayWidth, screenSize.width);
            }
            int diff = displayWidth - pw;
            pw = displayWidth;
            if (!this.comboBox.getComponentOrientation().isLeftToRight()) {
                px -= diff;
            }
        }
        return super.computePopupBounds(px, py, pw, ph);
    }

    @Override
    protected void configurePopup() {
        super.configurePopup();
        Border border = UIManager.getBorder("PopupMenu.border");
        if (border != null) {
            this.setBorder(border);
        }
    }

    @Override
    protected void configureList() {
        super.configureList();
        this.list.setCellRenderer(new PopupListCellRenderer());
        if (FlatComboBoxUI.this.popupBackground != null) {
            this.list.setBackground(FlatComboBoxUI.this.popupBackground);
        }
    }

    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        PropertyChangeListener superListener = super.createPropertyChangeListener();
        return e -> {
            superListener.propertyChange(e);
            if (e.getPropertyName() == "renderer") {
                this.list.setCellRenderer(new PopupListCellRenderer());
            }
        };
    }

    @Override
    protected int getPopupHeightForRowCount(int maxRowCount) {
        int height = super.getPopupHeightForRowCount(maxRowCount);
        FlatComboBoxUI.this.paddingBorder.uninstall();
        return height;
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        FlatComboBoxUI.this.paddingBorder.uninstall();
    }

    private class PopupListCellRenderer
    implements ListCellRenderer {
        private PopupListCellRenderer() {
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            FlatComboBoxUI.this.paddingBorder.uninstall();
            DefaultListCellRenderer renderer = FlatComboPopup.this.comboBox.getRenderer();
            if (renderer == null) {
                renderer = new DefaultListCellRenderer();
            }
            Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.applyComponentOrientation(FlatComboPopup.this.comboBox.getComponentOrientation());
            FlatComboBoxUI.this.paddingBorder.install(c);
            return c;
        }
    }
}
