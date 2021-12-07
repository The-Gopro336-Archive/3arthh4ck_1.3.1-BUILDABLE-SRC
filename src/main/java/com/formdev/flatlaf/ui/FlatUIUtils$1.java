package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Graphics2D;

class FlatUIUtils$1
extends Graphics2DProxy {
    FlatUIUtils$1(Graphics2D delegate) {
        super(delegate);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (height == 1) {
            height = Math.round(UIScale.scale(0.9f));
            y += height - 1;
        }
        super.fillRect(x, y, width, height);
    }
}
