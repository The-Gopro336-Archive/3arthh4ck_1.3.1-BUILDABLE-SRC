package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

class FlatTableHeaderUI$FlatTableCellHeaderRenderer
implements TableCellRenderer,
Border,
UIResource {
    private final TableCellRenderer delegate;
    private JLabel l;
    private int oldHorizontalTextPosition = -1;
    private Border origBorder;
    private Icon sortIcon;

    FlatTableHeaderUI$FlatTableCellHeaderRenderer(TableCellRenderer delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = this.delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!(c instanceof JLabel)) {
            return c;
        }
        this.l = (JLabel)c;
        if (FlatTableHeaderUI.this.sortIconPosition == 2) {
            if (this.oldHorizontalTextPosition < 0) {
                this.oldHorizontalTextPosition = this.l.getHorizontalTextPosition();
            }
            this.l.setHorizontalTextPosition(4);
        } else {
            this.sortIcon = this.l.getIcon();
            this.origBorder = this.l.getBorder();
            this.l.setIcon(null);
            this.l.setBorder(this);
        }
        return this.l;
    }

    void reset() {
        if (this.l != null && FlatTableHeaderUI.this.sortIconPosition == 2 && this.oldHorizontalTextPosition >= 0) {
            this.l.setHorizontalTextPosition(this.oldHorizontalTextPosition);
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.origBorder != null) {
            this.origBorder.paintBorder(c, g, x, y, width, height);
        }
        if (this.sortIcon != null) {
            int xi = x + (width - this.sortIcon.getIconWidth()) / 2;
            int yi = FlatTableHeaderUI.this.sortIconPosition == 1 ? y + UIScale.scale(1) : y + height - this.sortIcon.getIconHeight() - 1 - (int)(1.0f * UIScale.getUserScaleFactor());
            this.sortIcon.paintIcon(c, g, xi, yi);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return this.origBorder != null ? this.origBorder.getBorderInsets(c) : new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return this.origBorder != null ? this.origBorder.isBorderOpaque() : false;
    }

    static TableCellRenderer access$000(FlatTableHeaderUI$FlatTableCellHeaderRenderer x0) {
        return x0.delegate;
    }
}
