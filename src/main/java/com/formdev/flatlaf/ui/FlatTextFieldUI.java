package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatCaret;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.MigLayoutVisualPadding;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public class FlatTextFieldUI
extends BasicTextFieldUI {
    protected int minimumWidth;
    protected boolean isIntelliJTheme;
    protected Color placeholderForeground;
    protected Color focusedBackground;
    private Insets defaultMargin;
    private FocusListener focusListener;

    public static ComponentUI createUI(JComponent c) {
        return new FlatTextFieldUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        String prefix = this.getPropertyPrefix();
        this.minimumWidth = UIManager.getInt("Component.minimumWidth");
        this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
        this.placeholderForeground = UIManager.getColor(prefix + ".placeholderForeground");
        this.focusedBackground = UIManager.getColor(prefix + ".focusedBackground");
        this.defaultMargin = UIManager.getInsets(prefix + ".margin");
        LookAndFeel.installProperty(this.getComponent(), "opaque", false);
        MigLayoutVisualPadding.install(this.getComponent());
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.placeholderForeground = null;
        this.focusedBackground = null;
        MigLayoutVisualPadding.uninstall(this.getComponent());
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.focusListener = new FlatUIUtils.RepaintFocusListener(this.getComponent(), null);
        this.getComponent().addFocusListener(this.focusListener);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.getComponent().removeFocusListener(this.focusListener);
        this.focusListener = null;
    }

    @Override
    protected Caret createCaret() {
        return new FlatCaret(UIManager.getString("TextComponent.selectAllOnFocusPolicy"), UIManager.getBoolean("TextComponent.selectAllOnMouseClick"));
    }

    @Override
    protected void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        FlatTextFieldUI.propertyChange(this.getComponent(), e);
    }

    static void propertyChange(JTextComponent c, PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "JTextField.placeholderText": 
            case "JComponent.roundRect": 
            case "JTextField.padding": {
                c.repaint();
                break;
            }
            case "JComponent.minimumWidth": {
                c.revalidate();
            }
        }
    }

    @Override
    protected void paintSafely(Graphics g) {
        FlatTextFieldUI.paintBackground(g, this.getComponent(), this.isIntelliJTheme, this.focusedBackground);
        this.paintPlaceholder(g);
        super.paintSafely(HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g));
    }

    @Override
    protected void paintBackground(Graphics g) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void paintBackground(Graphics g, JTextComponent c, boolean isIntelliJTheme, Color focusedBackground) {
        if (!c.isOpaque() && FlatUIUtils.getOutsideFlatBorder(c) == null && FlatUIUtils.hasOpaqueBeenExplicitlySet(c)) {
            return;
        }
        float focusWidth = FlatUIUtils.getBorderFocusWidth(c);
        float arc = FlatUIUtils.getBorderArc(c);
        if (c.isOpaque() && (focusWidth > 0.0f || arc > 0.0f)) {
            FlatUIUtils.paintParentBackground(g, c);
        }
        Graphics2D g2 = (Graphics2D)g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            g2.setColor(FlatTextFieldUI.getBackground(c, isIntelliJTheme, focusedBackground));
            FlatUIUtils.paintComponentBackground(g2, 0, 0, c.getWidth(), c.getHeight(), focusWidth, arc);
        }
        finally {
            g2.dispose();
        }
    }

    static Color getBackground(JTextComponent c, boolean isIntelliJTheme, Color focusedBackground) {
        Color background = c.getBackground();
        if (!(background instanceof UIResource)) {
            return background;
        }
        if (focusedBackground != null && FlatUIUtils.isPermanentFocusOwner(c)) {
            return focusedBackground;
        }
        if (!(!isIntelliJTheme || c.isEnabled() && c.isEditable())) {
            return FlatUIUtils.getParentBackground(c);
        }
        return background;
    }

    protected void paintPlaceholder(Graphics g) {
        JTextComponent c = this.getComponent();
        if (c.getDocument().getLength() > 0) {
            return;
        }
        Container parent = c.getParent();
        JComponent jc = parent instanceof JComboBox ? (JComboBox)parent : c;
        Object placeholder = jc.getClientProperty("JTextField.placeholderText");
        if (!(placeholder instanceof String)) {
            return;
        }
        Rectangle r = this.getVisibleEditorRect();
        FontMetrics fm = c.getFontMetrics(c.getFont());
        int y = r.y + fm.getAscent() + (r.height - fm.getHeight()) / 2;
        g.setColor(this.placeholderForeground);
        String clippedPlaceholder = JavaCompatibility.getClippedString(c, fm, (String)placeholder, r.width);
        FlatUIUtils.drawString(c, g, clippedPlaceholder, r.x, y);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return this.applyMinimumWidth(c, super.getPreferredSize(c), this.minimumWidth);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return this.applyMinimumWidth(c, super.getMinimumSize(c), this.minimumWidth);
    }

    private Dimension applyMinimumWidth(JComponent c, Dimension size, int minimumWidth) {
        if (c instanceof JTextField && ((JTextField)c).getColumns() > 0) {
            return size;
        }
        if (!FlatTextFieldUI.hasDefaultMargins(c, this.defaultMargin)) {
            return size;
        }
        Container parent = c.getParent();
        if (parent instanceof JComboBox || parent instanceof JSpinner || parent != null && parent.getParent() instanceof JSpinner) {
            return size;
        }
        minimumWidth = FlatUIUtils.minimumWidth(c, minimumWidth);
        float focusWidth = FlatUIUtils.getBorderFocusWidth(c);
        size.width = Math.max(size.width, UIScale.scale(minimumWidth) + Math.round(focusWidth * 2.0f));
        return size;
    }

    static boolean hasDefaultMargins(JComponent c, Insets defaultMargin) {
        Insets margin = ((JTextComponent)c).getMargin();
        return margin instanceof UIResource && Objects.equals(margin, defaultMargin);
    }

    @Override
    protected Rectangle getVisibleEditorRect() {
        Insets padding;
        Rectangle r = super.getVisibleEditorRect();
        if (r != null && (padding = this.getPadding()) != null) {
            r = FlatUIUtils.subtractInsets(r, padding);
            r.width = Math.max(r.width, 0);
            r.height = Math.max(r.height, 0);
        }
        return r;
    }

    protected Insets getPadding() {
        Object padding = this.getComponent().getClientProperty("JTextField.padding");
        return padding instanceof Insets ? UIScale.scale((Insets)padding) : null;
    }

    protected void scrollCaretToVisible() {
        Caret caret = this.getComponent().getCaret();
        if (caret instanceof FlatCaret) {
            ((FlatCaret)caret).scrollCaretToVisible();
        }
    }
}
