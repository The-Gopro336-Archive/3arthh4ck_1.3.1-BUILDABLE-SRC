package com.formdev.flatlaf.ui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

class FlatTitlePane$2
extends JComponent {
    FlatTitlePane$2() {
    }

    @Override
    public Dimension getPreferredSize() {
        JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
        return FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar) ? menuBar.getPreferredSize() : new Dimension();
    }
}
