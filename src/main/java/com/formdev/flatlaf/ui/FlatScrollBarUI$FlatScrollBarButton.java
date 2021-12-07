package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;

class FlatScrollBarUI$FlatScrollBarButton
extends FlatArrowButton {
    protected FlatScrollBarUI$FlatScrollBarButton(int direction) {
        this(direction, this$0.arrowType, this$0.buttonArrowColor, this$0.buttonDisabledArrowColor, null, this$0.hoverButtonBackground, null, this$0.pressedButtonBackground);
    }

    protected FlatScrollBarUI$FlatScrollBarButton(int direction, String type, Color foreground, Color disabledForeground, Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground) {
        super(direction, type, foreground, disabledForeground, hoverForeground, hoverBackground, pressedForeground, pressedBackground);
        this.setArrowWidth(6);
        this.setFocusable(false);
        this.setRequestFocusEnabled(false);
    }

    @Override
    protected Color deriveBackground(Color background) {
        return FlatUIUtils.deriveColor(background, FlatScrollBarUI.this.scrollbar.getBackground());
    }

    @Override
    public Dimension getPreferredSize() {
        if (FlatScrollBarUI.this.isShowButtons()) {
            int w = UIScale.scale(FlatScrollBarUI.this.scrollBarWidth);
            return new Dimension(w, w);
        }
        return new Dimension();
    }

    @Override
    public Dimension getMinimumSize() {
        return FlatScrollBarUI.this.isShowButtons() ? super.getMinimumSize() : new Dimension();
    }

    @Override
    public Dimension getMaximumSize() {
        return FlatScrollBarUI.this.isShowButtons() ? super.getMaximumSize() : new Dimension();
    }
}
