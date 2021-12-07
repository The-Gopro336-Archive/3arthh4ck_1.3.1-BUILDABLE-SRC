package com.formdev.flatlaf.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class FlatFileChooserUI$1
implements TableCellRenderer {
    final TableCellRenderer val$defaultRenderer;

    FlatFileChooserUI$1(TableCellRenderer tableCellRenderer) {
        this.val$defaultRenderer = tableCellRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof String && ((String)value).startsWith("\u200e")) {
            String str = (String)value;
            char[] buf = new char[str.length()];
            int j = 0;
            for (int i = 0; i < buf.length; ++i) {
                char ch = str.charAt(i);
                if (ch == '\u200e' || ch == '\u200f') continue;
                buf[j++] = ch;
            }
            value = new String(buf, 0, j);
        }
        return this.val$defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
