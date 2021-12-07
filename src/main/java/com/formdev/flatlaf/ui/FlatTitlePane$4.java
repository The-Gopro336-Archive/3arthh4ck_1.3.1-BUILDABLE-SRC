package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JPanel;

class FlatTitlePane$4
extends JPanel {
    FlatTitlePane$4() {
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (FlatTitlePane.this.buttonMaximizedHeight > 0 && FlatTitlePane.this.window instanceof Frame && (((Frame)FlatTitlePane.this.window).getExtendedState() & 6) != 0) {
            size = new Dimension(size.width, Math.min(size.height, UIScale.scale(FlatTitlePane.this.buttonMaximizedHeight)));
        }
        return size;
    }
}
