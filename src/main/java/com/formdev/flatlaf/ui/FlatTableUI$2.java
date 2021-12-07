package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.StackUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.plaf.basic.BasicTableUI;

class FlatTableUI$2
extends Graphics2DProxy {
    final boolean val$hideLastVerticalLine;
    final boolean val$verticalLines;
    final int val$tableWidth;
    final boolean val$isDragging;
    final boolean val$horizontalLines;
    final double val$lineThickness;

    FlatTableUI$2(Graphics2D delegate, boolean bl, boolean bl2, int n, boolean bl3, boolean bl4, double d) {
        this.val$hideLastVerticalLine = bl;
        this.val$verticalLines = bl2;
        this.val$tableWidth = n;
        this.val$isDragging = bl3;
        this.val$horizontalLines = bl4;
        this.val$lineThickness = d;
        super(delegate);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (this.val$hideLastVerticalLine && this.val$verticalLines && x1 == x2 && y1 == 0 && x1 == this.val$tableWidth - 1 && this.wasInvokedFromPaintGrid()) {
            return;
        }
        if (this.val$isDragging && SystemInfo.isJava_9_orLater && (this.val$horizontalLines && y1 == y2 || this.val$verticalLines && x1 == x2) && this.wasInvokedFromMethod("paintDraggedArea")) {
            if (y1 == y2) {
                super.fill(new Rectangle2D.Double(x1, y1, x2 - x1 + 1, this.val$lineThickness));
            } else if (x1 == x2) {
                super.fill(new Rectangle2D.Double(x1, y1, this.val$lineThickness, y2 - y1 + 1));
            }
            return;
        }
        super.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (this.val$hideLastVerticalLine && this.val$verticalLines && width == 1 && y == 0 && x == this.val$tableWidth - 1 && this.wasInvokedFromPaintGrid()) {
            return;
        }
        if (this.val$lineThickness != 1.0) {
            if (this.val$horizontalLines && height == 1 && this.wasInvokedFromPaintGrid()) {
                super.fill(new Rectangle2D.Double(x, y, width, this.val$lineThickness));
                return;
            }
            if (this.val$verticalLines && width == 1 && y == 0 && this.wasInvokedFromPaintGrid()) {
                super.fill(new Rectangle2D.Double(x, y, this.val$lineThickness, height));
                return;
            }
        }
        super.fillRect(x, y, width, height);
    }

    private boolean wasInvokedFromPaintGrid() {
        return this.wasInvokedFromMethod("paintGrid");
    }

    private boolean wasInvokedFromMethod(String methodName) {
        return StackUtils.wasInvokedFrom(BasicTableUI.class.getName(), methodName, 8);
    }
}
