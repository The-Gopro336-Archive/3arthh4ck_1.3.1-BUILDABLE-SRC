package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.ScaledImageIcon;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.basic.BasicFileChooserUI;

class FlatFileChooserUI$FlatFileView
extends BasicFileChooserUI.BasicFileView {
    private FlatFileChooserUI$FlatFileView() {
        super(FlatFileChooserUI.this);
    }

    @Override
    public Icon getIcon(File f) {
        Icon icon = this.getCachedIcon(f);
        if (icon != null) {
            return icon;
        }
        if (f != null && (icon = FlatFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(f)) != null) {
            if (icon instanceof ImageIcon) {
                icon = new ScaledImageIcon((ImageIcon)icon);
            }
            this.cacheIcon(f, icon);
            return icon;
        }
        icon = super.getIcon(f);
        if (icon instanceof ImageIcon) {
            icon = new ScaledImageIcon((ImageIcon)icon);
            this.cacheIcon(f, icon);
        }
        return icon;
    }
}
