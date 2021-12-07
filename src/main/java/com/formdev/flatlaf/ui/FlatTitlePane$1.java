package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTitlePane;
import javax.swing.JLabel;

class FlatTitlePane$1
extends JLabel {
    FlatTitlePane$1() {
    }

    @Override
    public void updateUI() {
        this.setUI(new FlatTitlePane.FlatTitleLabelUI(FlatTitlePane.this));
    }
}
