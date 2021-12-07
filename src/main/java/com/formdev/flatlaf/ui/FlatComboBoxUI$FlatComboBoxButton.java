package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatArrowButton;
import java.awt.Color;

class FlatComboBoxUI$FlatComboBoxButton
extends FlatArrowButton {
    protected FlatComboBoxUI$FlatComboBoxButton() {
        this(5, this$0.arrowType, this$0.buttonArrowColor, this$0.buttonDisabledArrowColor, this$0.buttonHoverArrowColor, null, this$0.buttonPressedArrowColor, null);
    }

    protected FlatComboBoxUI$FlatComboBoxButton(int direction, String type, Color foreground, Color disabledForeground, Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground) {
        super(direction, type, foreground, disabledForeground, hoverForeground, hoverBackground, pressedForeground, pressedBackground);
    }

    @Override
    protected boolean isHover() {
        return super.isHover() || !FlatComboBoxUI.this.comboBox.isEditable() && FlatComboBoxUI.this.hover;
    }

    @Override
    protected boolean isPressed() {
        return super.isPressed() || !FlatComboBoxUI.this.comboBox.isEditable() && FlatComboBoxUI.this.pressed;
    }

    @Override
    protected Color getArrowColor() {
        if (FlatComboBoxUI.this.isCellRenderer() && FlatComboBoxUI.this.isCellRendererBackgroundChanged()) {
            return FlatComboBoxUI.this.comboBox.getForeground();
        }
        return super.getArrowColor();
    }
}
