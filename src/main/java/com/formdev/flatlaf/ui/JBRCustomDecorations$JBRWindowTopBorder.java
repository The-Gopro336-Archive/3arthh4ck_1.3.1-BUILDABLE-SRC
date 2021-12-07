package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;

class JBRCustomDecorations$JBRWindowTopBorder
extends BorderUIResource.EmptyBorderUIResource {
    private static JBRCustomDecorations$JBRWindowTopBorder instance;
    private final Color defaultActiveBorder = new Color(0x707070);
    private final Color inactiveLightColor = new Color(0xAAAAAA);
    private boolean colorizationAffectsBorders;
    private Color activeColor = this.defaultActiveBorder;

    static JBRCustomDecorations$JBRWindowTopBorder getInstance() {
        if (instance == null) {
            instance = new JBRCustomDecorations$JBRWindowTopBorder();
        }
        return instance;
    }

    JBRCustomDecorations$JBRWindowTopBorder() {
        super(1, 0, 0, 0);
        this.update();
        this.installListeners();
    }

    void update() {
        this.colorizationAffectsBorders = this.isColorizationColorAffectsBorders();
        this.activeColor = this.calculateActiveBorderColor();
    }

    void installListeners() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.addPropertyChangeListener("win.dwm.colorizationColor.affects.borders", e -> {
            this.colorizationAffectsBorders = this.isColorizationColorAffectsBorders();
            this.activeColor = this.calculateActiveBorderColor();
        });
        PropertyChangeListener l = e -> {
            this.activeColor = this.calculateActiveBorderColor();
        };
        toolkit.addPropertyChangeListener("win.dwm.colorizationColor", l);
        toolkit.addPropertyChangeListener("win.dwm.colorizationColorBalance", l);
        toolkit.addPropertyChangeListener("win.frame.activeBorderColor", l);
    }

    boolean isColorizationColorAffectsBorders() {
        Object value = Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColor.affects.borders");
        return value instanceof Boolean ? (Boolean)value : true;
    }

    Color getColorizationColor() {
        return (Color)Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColor");
    }

    int getColorizationColorBalance() {
        Object value = Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColorBalance");
        return value instanceof Integer ? (Integer)value : -1;
    }

    private Color calculateActiveBorderColor() {
        if (!this.colorizationAffectsBorders) {
            return this.defaultActiveBorder;
        }
        Color colorizationColor = this.getColorizationColor();
        if (colorizationColor != null) {
            int colorizationColorBalance = this.getColorizationColorBalance();
            if (colorizationColorBalance < 0 || colorizationColorBalance > 100) {
                colorizationColorBalance = 100;
            }
            if (colorizationColorBalance == 0) {
                return new Color(0xD9D9D9);
            }
            if (colorizationColorBalance == 100) {
                return colorizationColor;
            }
            float alpha = (float)colorizationColorBalance / 100.0f;
            float remainder = 1.0f - alpha;
            int r = Math.round((float)colorizationColor.getRed() * alpha + 217.0f * remainder);
            int g = Math.round((float)colorizationColor.getGreen() * alpha + 217.0f * remainder);
            int b = Math.round((float)colorizationColor.getBlue() * alpha + 217.0f * remainder);
            r = Math.min(Math.max(r, 0), 255);
            g = Math.min(Math.max(g, 0), 255);
            b = Math.min(Math.max(b, 0), 255);
            return new Color(r, g, b);
        }
        Color activeBorderColor = (Color)Toolkit.getDefaultToolkit().getDesktopProperty("win.frame.activeBorderColor");
        return activeBorderColor != null ? activeBorderColor : UIManager.getColor("MenuBar.borderColor");
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        boolean paintTopBorder;
        Window window = SwingUtilities.windowForComponent(c);
        boolean active = window != null ? window.isActive() : false;
        boolean bl = paintTopBorder = !FlatLaf.isLafDark() || active && this.colorizationAffectsBorders;
        if (!paintTopBorder) {
            return;
        }
        g.setColor(active ? this.activeColor : this.inactiveLightColor);
        HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, width, height, this::paintImpl);
    }

    private void paintImpl(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
        g.drawRect(x, y, width - 1, 0);
    }

    void repaintBorder(Component c) {
        c.repaint(0, 0, c.getWidth(), 1);
    }
}
