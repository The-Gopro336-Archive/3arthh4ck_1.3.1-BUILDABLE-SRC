/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTextFieldUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

public class FlatEditorPaneUI
extends BasicEditorPaneUI {
    protected int minimumWidth;
    protected boolean isIntelliJTheme;
    protected Color focusedBackground;
    private Insets defaultMargin;
    private Object oldHonorDisplayProperties;
    private FocusListener focusListener;

    public static ComponentUI createUI(JComponent c) {
        return new FlatEditorPaneUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        String prefix = this.getPropertyPrefix();
        this.minimumWidth = UIManager.getInt("Component.minimumWidth");
        this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
        this.focusedBackground = UIManager.getColor(prefix + ".focusedBackground");
        this.defaultMargin = UIManager.getInsets(prefix + ".margin");
        this.oldHonorDisplayProperties = this.getComponent().getClientProperty("JEditorPane.honorDisplayProperties");
        this.getComponent().putClientProperty("JEditorPane.honorDisplayProperties", true);
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.focusedBackground = null;
        this.getComponent().putClientProperty("JEditorPane.honorDisplayProperties", this.oldHonorDisplayProperties);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.focusListener = new FlatUIUtils.RepaintFocusListener(this.getComponent(), c -> this.focusedBackground != null);
        this.getComponent().addFocusListener(this.focusListener);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.getComponent().removeFocusListener(this.focusListener);
        this.focusListener = null;
    }

    @Override
    protected void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        FlatEditorPaneUI.propertyChange(this.getComponent(), e);
    }

    static void propertyChange(JTextComponent c, PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "JComponent.minimumWidth": {
                c.revalidate();
            }
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return FlatEditorPaneUI.applyMinimumWidth(c, super.getPreferredSize(c), this.minimumWidth, this.defaultMargin);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return FlatEditorPaneUI.applyMinimumWidth(c, super.getMinimumSize(c), this.minimumWidth, this.defaultMargin);
    }

    static Dimension applyMinimumWidth(JComponent c, Dimension size, int minimumWidth, Insets defaultMargin) {
        if (!FlatTextFieldUI.hasDefaultMargins(c, defaultMargin)) {
            return size;
        }
        minimumWidth = FlatUIUtils.minimumWidth(c, minimumWidth);
        size.width = Math.max(size.width, UIScale.scale(minimumWidth) - UIScale.scale(1) * 2);
        return size;
    }

    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g));
    }

    @Override
    protected void paintBackground(Graphics g) {
        FlatEditorPaneUI.paintBackground(g, this.getComponent(), this.isIntelliJTheme, this.focusedBackground);
    }

    static void paintBackground(Graphics g, JTextComponent c, boolean isIntelliJTheme, Color focusedBackground) {
        g.setColor(FlatTextFieldUI.getBackground(c, isIntelliJTheme, focusedBackground));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }
}

