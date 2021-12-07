/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.icons.FlatCheckBoxMenuItemIcon;
import java.awt.Graphics2D;

public class FlatRadioButtonMenuItemIcon
extends FlatCheckBoxMenuItemIcon {
    @Override
    protected void paintCheckmark(Graphics2D g2) {
        g2.fillOval(4, 4, 7, 7);
    }
}

