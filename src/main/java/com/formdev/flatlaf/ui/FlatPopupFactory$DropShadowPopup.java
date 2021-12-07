package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatPopupFactory;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

class FlatPopupFactory$DropShadowPopup
extends FlatPopupFactory.NonFlashingPopup {
    private final Component owner;
    private JComponent lightComp;
    private Border oldBorder;
    private boolean oldOpaque;
    private boolean mediumWeightShown;
    private Panel mediumWeightPanel;
    private JPanel dropShadowPanel;
    private ComponentListener mediumPanelListener;
    private Popup dropShadowDelegate;
    private Window dropShadowWindow;
    private Color oldDropShadowWindowBackground;

    FlatPopupFactory$DropShadowPopup(Popup delegate, Component owner, Component contents) {
        super(FlatPopupFactory.this, delegate, contents);
        this.owner = owner;
        Dimension size = contents.getPreferredSize();
        if (size.width <= 0 || size.height <= 0) {
            return;
        }
        if (this.popupWindow != null) {
            JPanel dropShadowPanel = new JPanel();
            dropShadowPanel.setBorder(this.createDropShadowBorder());
            dropShadowPanel.setOpaque(false);
            Dimension prefSize = this.popupWindow.getPreferredSize();
            Insets insets = dropShadowPanel.getInsets();
            dropShadowPanel.setPreferredSize(new Dimension(prefSize.width + insets.left + insets.right, prefSize.height + insets.top + insets.bottom));
            int x = this.popupWindow.getX() - insets.left;
            int y = this.popupWindow.getY() - insets.top;
            this.dropShadowDelegate = FlatPopupFactory.this.getPopupForScreenOfOwner(owner, dropShadowPanel, x, y, true);
            this.dropShadowWindow = SwingUtilities.windowForComponent(dropShadowPanel);
            if (this.dropShadowWindow != null) {
                this.oldDropShadowWindowBackground = this.dropShadowWindow.getBackground();
                this.dropShadowWindow.setBackground(new Color(0, true));
            }
        } else {
            this.mediumWeightPanel = (Panel)SwingUtilities.getAncestorOfClass(Panel.class, contents);
            if (this.mediumWeightPanel != null) {
                this.dropShadowPanel = new JPanel();
                this.dropShadowPanel.setBorder(this.createDropShadowBorder());
                this.dropShadowPanel.setOpaque(false);
                this.dropShadowPanel.setSize(FlatUIUtils.addInsets(this.mediumWeightPanel.getSize(), this.dropShadowPanel.getInsets()));
            } else {
                Container p = contents.getParent();
                if (!(p instanceof JComponent)) {
                    return;
                }
                this.lightComp = (JComponent)p;
                this.oldBorder = this.lightComp.getBorder();
                this.oldOpaque = this.lightComp.isOpaque();
                this.lightComp.setBorder(this.createDropShadowBorder());
                this.lightComp.setOpaque(false);
                this.lightComp.setSize(this.lightComp.getPreferredSize());
            }
        }
    }

    private Border createDropShadowBorder() {
        return new FlatDropShadowBorder(UIManager.getColor("Popup.dropShadowColor"), UIManager.getInsets("Popup.dropShadowInsets"), FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5f));
    }

    @Override
    public void show() {
        if (this.dropShadowDelegate != null) {
            FlatPopupFactory.showPopupAndFixLocation(this.dropShadowDelegate, this.dropShadowWindow);
        }
        if (this.mediumWeightPanel != null) {
            this.showMediumWeightDropShadow();
        }
        super.show();
        if (this.lightComp != null) {
            Insets insets = this.lightComp.getInsets();
            if (insets.left != 0 || insets.top != 0) {
                this.lightComp.setLocation(this.lightComp.getX() - insets.left, this.lightComp.getY() - insets.top);
            }
        }
    }

    @Override
    public void hide() {
        if (this.dropShadowDelegate != null) {
            this.dropShadowDelegate.hide();
            this.dropShadowDelegate = null;
        }
        if (this.mediumWeightPanel != null) {
            this.hideMediumWeightDropShadow();
            this.dropShadowPanel = null;
            this.mediumWeightPanel = null;
        }
        super.hide();
        if (this.dropShadowWindow != null) {
            this.dropShadowWindow.setBackground(this.oldDropShadowWindowBackground);
            this.dropShadowWindow = null;
        }
        if (this.lightComp != null) {
            this.lightComp.setBorder(this.oldBorder);
            this.lightComp.setOpaque(this.oldOpaque);
            this.lightComp = null;
        }
    }

    private void showMediumWeightDropShadow() {
        if (this.mediumWeightShown) {
            return;
        }
        this.mediumWeightShown = true;
        if (this.owner == null) {
            return;
        }
        Window window = SwingUtilities.windowForComponent(this.owner);
        if (!(window instanceof RootPaneContainer)) {
            return;
        }
        this.dropShadowPanel.setVisible(false);
        JLayeredPane layeredPane = ((RootPaneContainer)((Object)window)).getLayeredPane();
        layeredPane.add(this.dropShadowPanel, JLayeredPane.POPUP_LAYER, 0);
        this.moveMediumWeightDropShadow();
        this.resizeMediumWeightDropShadow();
        this.mediumPanelListener = new ComponentListener(){

            @Override
            public void componentShown(ComponentEvent e) {
                if (DropShadowPopup.this.dropShadowPanel != null) {
                    DropShadowPopup.this.dropShadowPanel.setVisible(true);
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (DropShadowPopup.this.dropShadowPanel != null) {
                    DropShadowPopup.this.dropShadowPanel.setVisible(false);
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                DropShadowPopup.this.moveMediumWeightDropShadow();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                DropShadowPopup.this.resizeMediumWeightDropShadow();
            }
        };
        this.mediumWeightPanel.addComponentListener(this.mediumPanelListener);
    }

    private void hideMediumWeightDropShadow() {
        this.mediumWeightPanel.removeComponentListener(this.mediumPanelListener);
        Container parent = this.dropShadowPanel.getParent();
        if (parent != null) {
            Rectangle bounds = this.dropShadowPanel.getBounds();
            parent.remove(this.dropShadowPanel);
            parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void moveMediumWeightDropShadow() {
        if (this.dropShadowPanel != null && this.mediumWeightPanel != null) {
            Point location = this.mediumWeightPanel.getLocation();
            Insets insets = this.dropShadowPanel.getInsets();
            this.dropShadowPanel.setLocation(location.x - insets.left, location.y - insets.top);
        }
    }

    private void resizeMediumWeightDropShadow() {
        if (this.dropShadowPanel != null && this.mediumWeightPanel != null) {
            this.dropShadowPanel.setSize(FlatUIUtils.addInsets(this.mediumWeightPanel.getSize(), this.dropShadowPanel.getInsets()));
        }
    }
}
