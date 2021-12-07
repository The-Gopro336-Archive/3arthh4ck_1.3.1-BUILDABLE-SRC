/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;

public class FlatSearchWithHistoryIcon
extends FlatSearchIcon {
    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.translate(-2, 0);
        super.paintIcon(c, g);
        g.translate(2, 0);
        g.fill(FlatUIUtils.createPath(11.0, 7.0, 16.0, 7.0, 13.5, 10.0));
    }
}

