package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class FlatMenuItemRenderer$MinSizeIcon
implements Icon {
    private final Icon delegate;

    FlatMenuItemRenderer$MinSizeIcon(Icon delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getIconWidth() {
        int iconWidth = this.delegate != null ? this.delegate.getIconWidth() : 0;
        return Math.max(iconWidth, UIScale.scale(FlatMenuItemRenderer.this.minimumIconSize.width));
    }

    @Override
    public int getIconHeight() {
        int iconHeight = this.delegate != null ? this.delegate.getIconHeight() : 0;
        return Math.max(iconHeight, UIScale.scale(FlatMenuItemRenderer.this.minimumIconSize.height));
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
    }
}
