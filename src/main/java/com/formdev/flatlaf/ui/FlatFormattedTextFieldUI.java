/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTextFieldUI;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class FlatFormattedTextFieldUI
extends FlatTextFieldUI {
    public static ComponentUI createUI(JComponent c) {
        return new FlatFormattedTextFieldUI();
    }

    @Override
    protected String getPropertyPrefix() {
        return "FormattedTextField";
    }
}

