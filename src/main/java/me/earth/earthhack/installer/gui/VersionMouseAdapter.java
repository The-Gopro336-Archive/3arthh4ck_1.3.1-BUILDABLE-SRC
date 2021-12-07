/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;

public class VersionMouseAdapter
extends MouseAdapter {
    private final JTable table;
    private final JButton install;
    private final JButton uninstall;
    private final Object[][] data;

    public VersionMouseAdapter(JTable table, JButton install, JButton uninstall, Object[][] data) {
        this.table = table;
        this.install = install;
        this.uninstall = uninstall;
        this.data = data;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        int row = this.table.rowAtPoint(evt.getPoint());
        if (row >= 0) {
            Object[] o = this.data[row];
            if (!((Boolean)o[3]).booleanValue()) {
                this.install.setEnabled(false);
                this.uninstall.setEnabled(false);
            } else if (((Boolean)o[2]).booleanValue()) {
                this.install.setEnabled(false);
                this.uninstall.setEnabled(true);
            } else {
                this.install.setEnabled(true);
                this.uninstall.setEnabled(false);
            }
        } else {
            this.install.setEnabled(false);
            this.uninstall.setEnabled(false);
        }
    }
}

