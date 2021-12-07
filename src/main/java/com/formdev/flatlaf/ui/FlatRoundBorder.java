/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import javax.swing.UIManager;

public class FlatRoundBorder
extends FlatBorder {
    protected final int arc = UIManager.getInt("Component.arc");

    @Override
    protected int getArc(Component c) {
        if (this.isCellEditor(c)) {
            return 0;
        }
        Boolean roundRect = FlatUIUtils.isRoundRect(c);
        return roundRect != null ? (roundRect.booleanValue() ? 32767 : 0) : this.arc;
    }
}

