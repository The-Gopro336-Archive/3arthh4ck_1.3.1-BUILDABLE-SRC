package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.JBRCustomDecorations;
import java.awt.Color;
import java.awt.Window;

class FlatNativeWindowBorder$WindowTopBorder
extends JBRCustomDecorations.JBRWindowTopBorder {
    private static FlatNativeWindowBorder$WindowTopBorder instance;

    FlatNativeWindowBorder$WindowTopBorder() {
    }

    static JBRCustomDecorations.JBRWindowTopBorder getInstance() {
        if (canUseJBRCustomDecorations) {
            return JBRCustomDecorations.JBRWindowTopBorder.getInstance();
        }
        if (instance == null) {
            instance = new FlatNativeWindowBorder$WindowTopBorder();
        }
        return instance;
    }

    @Override
    void installListeners() {
        nativeProvider.addChangeListener(e -> {
            this.update();
            for (Window window : Window.getWindows()) {
                if (!window.isDisplayable()) continue;
                window.repaint(0, 0, window.getWidth(), 1);
            }
        });
    }

    @Override
    boolean isColorizationColorAffectsBorders() {
        return nativeProvider.isColorizationColorAffectsBorders();
    }

    @Override
    Color getColorizationColor() {
        return nativeProvider.getColorizationColor();
    }

    @Override
    int getColorizationColorBalance() {
        return nativeProvider.getColorizationColorBalance();
    }
}
