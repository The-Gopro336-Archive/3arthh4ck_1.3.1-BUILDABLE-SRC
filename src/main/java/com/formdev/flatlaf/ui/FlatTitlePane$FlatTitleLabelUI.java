package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatLabelUI;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JLabel;

class FlatTitlePane$FlatTitleLabelUI
extends FlatLabelUI {
    protected FlatTitlePane$FlatTitleLabelUI() {
    }

    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        boolean center;
        boolean hasEmbeddedMenuBar = FlatTitlePane.this.hasVisibleEmbeddedMenuBar(FlatTitlePane.this.rootPane.getJMenuBar());
        int labelWidth = l.getWidth();
        int textWidth = labelWidth - textX * 2;
        int gap = UIScale.scale(FlatTitlePane.this.menuBarTitleGap);
        boolean bl = center = hasEmbeddedMenuBar ? FlatTitlePane.this.centerTitleIfMenuBarEmbedded : FlatTitlePane.this.centerTitle;
        if (center) {
            int centeredTextX = (l.getParent().getWidth() - textWidth) / 2 - l.getX();
            if (centeredTextX >= gap && centeredTextX + textWidth <= labelWidth - gap) {
                textX = centeredTextX;
            }
        } else {
            int leadingTextX;
            boolean leftToRight = FlatTitlePane.this.getComponentOrientation().isLeftToRight();
            Insets insets = l.getInsets();
            int leadingInset = hasEmbeddedMenuBar ? gap : (leftToRight ? insets.left : insets.right);
            int n = leadingTextX = leftToRight ? leadingInset : labelWidth - leadingInset - textWidth;
            if (leftToRight ? leadingTextX < textX : leadingTextX > textX) {
                textX = leadingTextX;
            }
        }
        super.paintEnabledText(l, g, s, textX, textY);
    }
}
