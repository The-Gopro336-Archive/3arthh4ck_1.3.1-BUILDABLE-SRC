package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

class FlatWindowResizer$DragBorderComponent
extends JComponent
implements MouseListener,
MouseMotionListener {
    private final int leadingResizeDir;
    private final int centerResizeDir;
    private final int trailingResizeDir;
    private int resizeDir = -1;
    private int leadingCornerDragWidth;
    private int trailingCornerDragWidth;
    private int dragLeftOffset;
    private int dragRightOffset;
    private int dragTopOffset;
    private int dragBottomOffset;

    protected FlatWindowResizer$DragBorderComponent(int leadingResizeDir, int centerResizeDir, int trailingResizeDir) {
        this.leadingResizeDir = leadingResizeDir;
        this.centerResizeDir = centerResizeDir;
        this.trailingResizeDir = trailingResizeDir;
        this.setResizeDir(centerResizeDir);
        this.setVisible(false);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    void setCornerDragWidths(int leading, int trailing) {
        this.leadingCornerDragWidth = leading;
        this.trailingCornerDragWidth = trailing;
    }

    protected void setResizeDir(int resizeDir) {
        if (this.resizeDir == resizeDir) {
            return;
        }
        this.resizeDir = resizeDir;
        this.setCursor(Cursor.getPredefinedCursor(resizeDir));
    }

    @Override
    public Dimension getPreferredSize() {
        int thickness = UIScale.scale(FlatWindowResizer.this.borderDragThickness);
        return new Dimension(thickness, thickness);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintChildren(g);
        FlatWindowResizer.this.updateVisibility();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!FlatWindowResizer.this.isWindowResizable()) {
            return;
        }
        int xOnScreen = e.getXOnScreen();
        int yOnScreen = e.getYOnScreen();
        Rectangle windowBounds = FlatWindowResizer.this.getWindowBounds();
        this.dragLeftOffset = xOnScreen - windowBounds.x;
        this.dragTopOffset = yOnScreen - windowBounds.y;
        this.dragRightOffset = windowBounds.x + windowBounds.width - xOnScreen;
        this.dragBottomOffset = windowBounds.y + windowBounds.height - yOnScreen;
        int direction = 0;
        switch (this.resizeDir) {
            case 8: {
                direction = 1;
                break;
            }
            case 9: {
                direction = 5;
                break;
            }
            case 10: {
                direction = 7;
                break;
            }
            case 11: {
                direction = 3;
                break;
            }
            case 6: {
                direction = 8;
                break;
            }
            case 7: {
                direction = 2;
                break;
            }
            case 4: {
                direction = 6;
                break;
            }
            case 5: {
                direction = 4;
            }
        }
        FlatWindowResizer.this.beginResizing(direction);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!FlatWindowResizer.this.isWindowResizable()) {
            return;
        }
        this.dragBottomOffset = 0;
        this.dragTopOffset = 0;
        this.dragRightOffset = 0;
        this.dragLeftOffset = 0;
        FlatWindowResizer.this.endResizing();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int wh;
        boolean topOrBottom = this.centerResizeDir == 8 || this.centerResizeDir == 9;
        int xy = topOrBottom ? e.getX() : e.getY();
        int n = wh = topOrBottom ? this.getWidth() : this.getHeight();
        this.setResizeDir(xy <= this.leadingCornerDragWidth ? this.leadingResizeDir : (xy >= wh - this.trailingCornerDragWidth ? this.trailingResizeDir : this.centerResizeDir));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Dimension minimumSize;
        if (!FlatWindowResizer.this.isWindowResizable()) {
            return;
        }
        int xOnScreen = e.getXOnScreen();
        int yOnScreen = e.getYOnScreen();
        Rectangle oldBounds = FlatWindowResizer.this.getWindowBounds();
        Rectangle newBounds = new Rectangle(oldBounds);
        if (this.resizeDir == 8 || this.resizeDir == 6 || this.resizeDir == 7) {
            newBounds.y = yOnScreen - this.dragTopOffset;
            if (FlatWindowResizer.this.limitToParentBounds() && newBounds.y < 0) {
                newBounds.y = 0;
            }
            newBounds.height += oldBounds.y - newBounds.y;
        }
        if (this.resizeDir == 9 || this.resizeDir == 4 || this.resizeDir == 5) {
            int parentHeight;
            newBounds.height = yOnScreen + this.dragBottomOffset - newBounds.y;
            if (FlatWindowResizer.this.limitToParentBounds() && newBounds.y + newBounds.height > (parentHeight = FlatWindowResizer.this.getParentBounds().height)) {
                newBounds.height = parentHeight - newBounds.y;
            }
        }
        if (this.resizeDir == 10 || this.resizeDir == 6 || this.resizeDir == 4) {
            newBounds.x = xOnScreen - this.dragLeftOffset;
            if (FlatWindowResizer.this.limitToParentBounds() && newBounds.x < 0) {
                newBounds.x = 0;
            }
            newBounds.width += oldBounds.x - newBounds.x;
        }
        if (this.resizeDir == 11 || this.resizeDir == 7 || this.resizeDir == 5) {
            int parentWidth;
            newBounds.width = xOnScreen + this.dragRightOffset - newBounds.x;
            if (FlatWindowResizer.this.limitToParentBounds() && newBounds.x + newBounds.width > (parentWidth = FlatWindowResizer.this.getParentBounds().width)) {
                newBounds.width = parentWidth - newBounds.x;
            }
        }
        Dimension dimension = minimumSize = FlatWindowResizer.this.honorMinimumSizeOnResize() ? FlatWindowResizer.this.getWindowMinimumSize() : null;
        if (minimumSize == null) {
            minimumSize = UIScale.scale(new Dimension(150, 50));
        }
        if (newBounds.width < minimumSize.width) {
            this.changeWidth(oldBounds, newBounds, minimumSize.width);
        }
        if (newBounds.height < minimumSize.height) {
            this.changeHeight(oldBounds, newBounds, minimumSize.height);
        }
        if (FlatWindowResizer.this.honorMaximumSizeOnResize()) {
            Dimension maximumSize = FlatWindowResizer.this.getWindowMaximumSize();
            if (newBounds.width > maximumSize.width) {
                this.changeWidth(oldBounds, newBounds, maximumSize.width);
            }
            if (newBounds.height > maximumSize.height) {
                this.changeHeight(oldBounds, newBounds, maximumSize.height);
            }
        }
        if (!newBounds.equals(oldBounds)) {
            FlatWindowResizer.this.setWindowBounds(newBounds);
        }
    }

    private void changeWidth(Rectangle oldBounds, Rectangle newBounds, int width) {
        if (newBounds.x != oldBounds.x) {
            newBounds.x -= width - newBounds.width;
        }
        newBounds.width = width;
    }

    private void changeHeight(Rectangle oldBounds, Rectangle newBounds, int height) {
        if (newBounds.y != oldBounds.y) {
            newBounds.y -= height - newBounds.height;
        }
        newBounds.height = height;
    }
}
