package com.formdev.flatlaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class FlatPopupMenuUI
extends BasicPopupMenuUI {
    public static ComponentUI createUI(JComponent c) {
        return new FlatPopupMenuUI();
    }
}
