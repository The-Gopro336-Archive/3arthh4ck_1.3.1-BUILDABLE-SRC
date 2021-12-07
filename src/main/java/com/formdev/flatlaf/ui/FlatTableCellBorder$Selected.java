package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTableCellBorder;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class FlatTableCellBorder$Selected
extends FlatTableCellBorder {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        JTable table;
        if (!this.showCellFocusIndicator && (table = (JTable)SwingUtilities.getAncestorOfClass(JTable.class, c)) != null && !this.isSelectionEditable(table)) {
            return;
        }
        super.paintBorder(c, g, x, y, width, height);
    }

    protected boolean isSelectionEditable(JTable table) {
        if (table.getRowSelectionAllowed()) {
            int[] selectedRows;
            int columnCount = table.getColumnCount();
            for (int selectedRow : selectedRows = table.getSelectedRows()) {
                for (int column = 0; column < columnCount; ++column) {
                    if (!table.isCellEditable(selectedRow, column)) continue;
                    return true;
                }
            }
        }
        if (table.getColumnSelectionAllowed()) {
            int rowCount = table.getRowCount();
            int[] selectedColumns = table.getSelectedColumns();
            for (int selectedColumn : selectedColumns) {
                for (int row = 0; row < rowCount; ++row) {
                    if (!table.isCellEditable(row, selectedColumn)) continue;
                    return true;
                }
            }
        }
        return false;
    }
}
