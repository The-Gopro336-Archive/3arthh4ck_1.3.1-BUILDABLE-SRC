/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class FlatTableHeaderBorder
extends FlatEmptyBorder {
    protected Color separatorColor = UIManager.getColor("TableHeader.separatorColor");
    protected Color bottomSeparatorColor = UIManager.getColor("TableHeader.bottomSeparatorColor");

    public FlatTableHeaderBorder() {
        super(UIManager.getInsets("TableHeader.cellMargins"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        JTableHeader header = (JTableHeader)SwingUtilities.getAncestorOfClass(JTableHeader.class, c);
        boolean leftToRight = (header != null ? header : c).getComponentOrientation().isLeftToRight();
        boolean paintLeft = !leftToRight;
        boolean paintRight = leftToRight;
        if (header != null) {
            int hx = SwingUtilities.convertPoint((Component)c, (int)x, (int)y, (Component)header).x;
            if (this.isDraggedColumn(header, hx)) {
                paintRight = true;
                paintLeft = true;
            } else {
                if (hx <= 0 && !leftToRight && this.hideTrailingVerticalLine(header)) {
                    paintLeft = false;
                }
                if (hx + width >= header.getWidth() && leftToRight && this.hideTrailingVerticalLine(header)) {
                    paintRight = false;
                }
            }
        }
        float lineWidth = UIScale.scale(1.0f);
        Graphics2D g2 = (Graphics2D)g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            g2.setColor(this.separatorColor);
            if (paintLeft) {
                g2.fill(new Rectangle2D.Float(x, y, lineWidth, (float)height - lineWidth));
            }
            if (paintRight) {
                g2.fill(new Rectangle2D.Float((float)(x + width) - lineWidth, y, lineWidth, (float)height - lineWidth));
            }
            g2.setColor(this.bottomSeparatorColor);
            g2.fill(new Rectangle2D.Float(x, (float)(y + height) - lineWidth, width, lineWidth));
        }
        finally {
            g2.dispose();
        }
    }

    protected boolean isDraggedColumn(JTableHeader header, int x) {
        TableColumn draggedColumn = header.getDraggedColumn();
        if (draggedColumn == null) {
            return false;
        }
        int draggedDistance = header.getDraggedDistance();
        if (draggedDistance == 0) {
            return false;
        }
        int columnCount = header.getColumnModel().getColumnCount();
        for (int i = 0; i < columnCount; ++i) {
            if (header.getHeaderRect((int)i).x + draggedDistance != x) continue;
            return true;
        }
        return false;
    }

    protected boolean hideTrailingVerticalLine(JTableHeader header) {
        Container viewportParent;
        Container viewport = header.getParent();
        Container container = viewportParent = viewport != null ? viewport.getParent() : null;
        if (!(viewportParent instanceof JScrollPane)) {
            return true;
        }
        JScrollBar vsb = ((JScrollPane)viewportParent).getVerticalScrollBar();
        if (vsb == null || !vsb.isVisible()) {
            return true;
        }
        return vsb.getY() == viewport.getY();
    }
}

