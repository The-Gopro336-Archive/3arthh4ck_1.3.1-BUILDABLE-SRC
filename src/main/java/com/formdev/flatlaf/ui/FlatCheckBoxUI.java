/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatRadioButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class FlatCheckBoxUI
extends FlatRadioButtonUI {
    public static ComponentUI createUI(JComponent c) {
        return FlatUIUtils.createSharedUI(FlatCheckBoxUI.class, FlatCheckBoxUI::new);
    }

    @Override
    public String getPropertyPrefix() {
        return "CheckBox.";
    }
}

