package com.formdev.flatlaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

public class FlatColorChooserUI
extends BasicColorChooserUI {
    public static ComponentUI createUI(JComponent c) {
        return new FlatColorChooserUI();
    }
}
