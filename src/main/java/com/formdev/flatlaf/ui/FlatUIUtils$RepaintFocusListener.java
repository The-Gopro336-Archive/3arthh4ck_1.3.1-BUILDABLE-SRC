package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Predicate;

public class FlatUIUtils$RepaintFocusListener
implements FocusListener {
    private final Component repaintComponent;
    private final Predicate<Component> repaintCondition;

    public FlatUIUtils$RepaintFocusListener(Component repaintComponent, Predicate<Component> repaintCondition) {
        this.repaintComponent = repaintComponent;
        this.repaintCondition = repaintCondition;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.repaintCondition == null || this.repaintCondition.test(this.repaintComponent)) {
            this.repaintComponent.repaint();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.repaintCondition == null || this.repaintCondition.test(this.repaintComponent)) {
            this.repaintComponent.repaint();
        }
    }
}
