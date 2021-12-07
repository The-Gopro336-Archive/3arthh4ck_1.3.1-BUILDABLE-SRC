package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatEditorPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class FlatTextAreaUI
extends BasicTextAreaUI {
    protected int minimumWidth;
    protected boolean isIntelliJTheme;
    protected Color background;
    protected Color disabledBackground;
    protected Color inactiveBackground;
    protected Color focusedBackground;
    private Insets defaultMargin;
    private FocusListener focusListener;

    public static ComponentUI createUI(JComponent c) {
        return new FlatTextAreaUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        this.updateBackground();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.minimumWidth = UIManager.getInt("Component.minimumWidth");
        this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
        this.background = UIManager.getColor("TextArea.background");
        this.disabledBackground = UIManager.getColor("TextArea.disabledBackground");
        this.inactiveBackground = UIManager.getColor("TextArea.inactiveBackground");
        this.focusedBackground = UIManager.getColor("TextArea.focusedBackground");
        this.defaultMargin = UIManager.getInsets("TextArea.margin");
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.background = null;
        this.disabledBackground = null;
        this.inactiveBackground = null;
        this.focusedBackground = null;
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
        switch (e.getPropertyName()) {
            case "editable": 
            case "enabled": {
                this.updateBackground();
            }
        }
    }

    private void updateBackground() {
        Color newBackground;
        JTextComponent c = this.getComponent();
        Color background = c.getBackground();
        if (!(background instanceof UIResource)) {
            return;
        }
        if (background != this.background && background != this.disabledBackground && background != this.inactiveBackground) {
            return;
        }
        Color color = !c.isEnabled() ? this.disabledBackground : (newBackground = !c.isEditable() ? this.inactiveBackground : this.background);
        if (newBackground != background) {
            c.setBackground(newBackground);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return this.applyMinimumWidth(c, super.getPreferredSize(c));
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return this.applyMinimumWidth(c, super.getMinimumSize(c));
    }

    private Dimension applyMinimumWidth(JComponent c, Dimension size) {
        if (c instanceof JTextArea && ((JTextArea)c).getColumns() > 0) {
            return size;
        }
        return FlatEditorPaneUI.applyMinimumWidth(c, size, this.minimumWidth, this.defaultMargin);
    }

    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g));
    }

    @Override
    protected void paintBackground(Graphics g) {
        FlatEditorPaneUI.paintBackground(g, this.getComponent(), this.isIntelliJTheme, this.focusedBackground);
    }
}
