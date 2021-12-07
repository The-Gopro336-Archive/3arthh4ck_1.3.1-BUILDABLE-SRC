/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class FlatTableHeaderUI
extends BasicTableHeaderUI {
    protected Color bottomSeparatorColor;
    protected int height;
    protected int sortIconPosition;

    public static ComponentUI createUI(JComponent c) {
        return new FlatTableHeaderUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.bottomSeparatorColor = UIManager.getColor("TableHeader.bottomSeparatorColor");
        this.height = UIManager.getInt("TableHeader.height");
        switch (Objects.toString(UIManager.getString("TableHeader.sortIconPosition"), "right")) {
            default: {
                this.sortIconPosition = 4;
                break;
            }
            case "left": {
                this.sortIconPosition = 2;
                break;
            }
            case "top": {
                this.sortIconPosition = 1;
                break;
            }
            case "bottom": {
                this.sortIconPosition = 3;
            }
        }
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.bottomSeparatorColor = null;
    }

    @Override
    public int getRolloverColumn() {
        return super.getRolloverColumn();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        TableColumnModel columnModel = this.header.getColumnModel();
        if (columnModel.getColumnCount() <= 0) {
            return;
        }
        int columnCount = columnModel.getColumnCount();
        int totalWidth = 0;
        for (int i = 0; i < columnCount; ++i) {
            totalWidth += columnModel.getColumn(i).getWidth();
        }
        if (totalWidth < this.header.getWidth()) {
            TableCellRenderer defaultRenderer = this.header.getDefaultRenderer();
            boolean paintBottomSeparator = this.isSystemDefaultRenderer(defaultRenderer);
            if (!paintBottomSeparator && this.header.getTable() != null) {
                Component rendererComponent = defaultRenderer.getTableCellRendererComponent(this.header.getTable(), "", false, false, -1, 0);
                paintBottomSeparator = this.isSystemDefaultRenderer(rendererComponent);
            }
            if (paintBottomSeparator) {
                int w = c.getWidth() - totalWidth;
                int x = this.header.getComponentOrientation().isLeftToRight() ? c.getWidth() - w : 0;
                this.paintBottomSeparator(g, c, x, w);
            }
        }
        FlatTableCellHeaderRenderer sortIconRenderer = null;
        if (this.sortIconPosition != 4) {
            sortIconRenderer = new FlatTableCellHeaderRenderer(this.header.getDefaultRenderer());
            this.header.setDefaultRenderer(sortIconRenderer);
        }
        super.paint(g, c);
        if (sortIconRenderer != null) {
            sortIconRenderer.reset();
            this.header.setDefaultRenderer(sortIconRenderer.delegate);
        }
    }

    private boolean isSystemDefaultRenderer(Object headerRenderer) {
        String rendererClassName = headerRenderer.getClass().getName();
        return rendererClassName.equals("sun.swing.table.DefaultTableCellHeaderRenderer") || rendererClassName.equals("sun.swing.FilePane$AlignableTableHeaderRenderer");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void paintBottomSeparator(Graphics g, JComponent c, int x, int w) {
        float lineWidth = UIScale.scale(1.0f);
        Graphics2D g2 = (Graphics2D)g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            g2.setColor(this.bottomSeparatorColor);
            g2.fill(new Rectangle2D.Float(x, (float)c.getHeight() - lineWidth, w, lineWidth));
        }
        finally {
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension size = super.getPreferredSize(c);
        if (size.height > 0) {
            size.height = Math.max(size.height, UIScale.scale(this.height));
        }
        return size;
    }

    private class FlatTableCellHeaderRenderer
    implements TableCellRenderer,
    Border,
    UIResource {
        private final TableCellRenderer delegate;
        private JLabel l;
        private int oldHorizontalTextPosition = -1;
        private Border origBorder;
        private Icon sortIcon;

        FlatTableCellHeaderRenderer(TableCellRenderer delegate) {
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
    }
}

