package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatToggleButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class FlatButtonBorder
extends FlatBorder {
    protected final Color borderColor = FlatUIUtils.getUIColor("Button.startBorderColor", "Button.borderColor");
    protected final Color endBorderColor = UIManager.getColor("Button.endBorderColor");
    protected final Color disabledBorderColor = UIManager.getColor("Button.disabledBorderColor");
    protected final Color focusedBorderColor = UIManager.getColor("Button.focusedBorderColor");
    protected final Color hoverBorderColor = UIManager.getColor("Button.hoverBorderColor");
    protected final Color defaultBorderColor = FlatUIUtils.getUIColor("Button.default.startBorderColor", "Button.default.borderColor");
    protected final Color defaultEndBorderColor = UIManager.getColor("Button.default.endBorderColor");
    protected final Color defaultHoverBorderColor = UIManager.getColor("Button.default.hoverBorderColor");
    protected final Color defaultFocusedBorderColor = UIManager.getColor("Button.default.focusedBorderColor");
    protected final Color defaultFocusColor = UIManager.getColor("Button.default.focusColor");
    protected final Color toolbarFocusColor = UIManager.getColor("Button.toolbar.focusColor");
    protected final int borderWidth = UIManager.getInt("Button.borderWidth");
    protected final int defaultBorderWidth = UIManager.getInt("Button.default.borderWidth");
    protected final float buttonInnerFocusWidth = FlatUIUtils.getUIFloat("Button.innerFocusWidth", this.innerFocusWidth);
    protected final Insets toolbarMargin = UIManager.getInsets("Button.toolbar.margin");
    protected final Insets toolbarSpacingInsets = UIManager.getInsets("Button.toolbar.spacingInsets");
    protected final float toolbarFocusWidth = FlatUIUtils.getUIFloat("Button.toolbar.focusWidth", 1.5f);
    protected final int arc = UIManager.getInt("Button.arc");

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (!(!FlatButtonUI.isContentAreaFilled(c) || FlatButtonUI.isToolBarButton(c) || FlatButtonUI.isBorderlessButton(c) && !FlatUIUtils.isPermanentFocusOwner(c) || FlatButtonUI.isHelpButton(c) || FlatToggleButtonUI.isTabButton(c))) {
            super.paintBorder(c, g, x, y, width, height);
        } else if (FlatButtonUI.isToolBarButton(c) && this.isFocused(c)) {
            this.paintToolBarFocus(c, g, x, y, width, height);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void paintToolBarFocus(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            float focusWidth = UIScale.scale(this.toolbarFocusWidth);
            float arc = UIScale.scale((float)this.getArc(c));
            Color outlineColor = this.getOutlineColor(c);
            Insets spacing = UIScale.scale(this.toolbarSpacingInsets);
            g2.setColor(outlineColor != null ? outlineColor : this.getFocusColor(c));
            FlatUIUtils.paintComponentBorder(g2, x += spacing.left, y += spacing.top, width -= spacing.left + spacing.right, height -= spacing.top + spacing.bottom, 0.0f, focusWidth, arc);
        }
        finally {
            g2.dispose();
        }
    }

    @Override
    protected Color getFocusColor(Component c) {
        return this.toolbarFocusColor != null && FlatButtonUI.isToolBarButton(c) ? this.toolbarFocusColor : (FlatButtonUI.isDefaultButton(c) ? this.defaultFocusColor : super.getFocusColor(c));
    }

    @Override
    protected boolean isFocused(Component c) {
        return FlatButtonUI.isFocusPainted(c) && super.isFocused(c);
    }

    @Override
    protected Paint getBorderColor(Component c) {
        Color endBg;
        boolean def = FlatButtonUI.isDefaultButton(c);
        Paint color = FlatButtonUI.buttonStateColor(c, def ? this.defaultBorderColor : this.borderColor, this.disabledBorderColor, def ? this.defaultFocusedBorderColor : this.focusedBorderColor, def ? this.defaultHoverBorderColor : this.hoverBorderColor, null);
        Color startBg = def ? this.defaultBorderColor : this.borderColor;
        Color color2 = endBg = def ? this.defaultEndBorderColor : this.endBorderColor;
        if (color == startBg && endBg != null && !startBg.equals(endBg)) {
            color = new GradientPaint(0.0f, 0.0f, startBg, 0.0f, c.getHeight(), endBg);
        }
        return color;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        if (FlatButtonUI.isToolBarButton(c)) {
            Insets margin = c instanceof AbstractButton ? ((AbstractButton)c).getMargin() : null;
            FlatUIUtils.setInsets(insets, UIScale.scale(FlatUIUtils.addInsets(this.toolbarSpacingInsets, margin != null && !(margin instanceof UIResource) ? margin : this.toolbarMargin)));
        } else {
            insets = super.getBorderInsets(c, insets);
            if (FlatButtonUI.isIconOnlyOrSingleCharacterButton(c) && ((AbstractButton)c).getMargin() instanceof UIResource) {
                insets.left = insets.right = Math.min(insets.top, insets.bottom);
            }
        }
        return insets;
    }

    @Override
    protected int getFocusWidth(Component c) {
        return FlatToggleButtonUI.isTabButton(c) ? 0 : super.getFocusWidth(c);
    }

    @Override
    protected float getInnerFocusWidth(Component c) {
        return this.buttonInnerFocusWidth;
    }

    @Override
    protected int getBorderWidth(Component c) {
        return FlatButtonUI.isDefaultButton(c) ? this.defaultBorderWidth : this.borderWidth;
    }

    @Override
    protected int getArc(Component c) {
        if (this.isCellEditor(c)) {
            return 0;
        }
        switch (FlatButtonUI.getButtonType(c)) {
            case 0: {
                return 0;
            }
            case 1: {
                return Short.MAX_VALUE;
            }
        }
        return this.arc;
    }
}
