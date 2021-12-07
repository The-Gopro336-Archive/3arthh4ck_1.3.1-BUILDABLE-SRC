package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;

class FlatTitlePane$Handler
extends WindowAdapter
implements PropertyChangeListener,
MouseListener,
MouseMotionListener,
ComponentListener {
    private Point dragOffset;

    protected FlatTitlePane$Handler() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "title": {
                FlatTitlePane.this.titleLabel.setText(FlatTitlePane.this.getWindowTitle());
                break;
            }
            case "resizable": {
                if (!(FlatTitlePane.this.window instanceof Frame)) break;
                FlatTitlePane.this.frameStateChanged();
                break;
            }
            case "iconImage": {
                FlatTitlePane.this.updateIcon();
                break;
            }
            case "componentOrientation": {
                FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpotsLater();
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        FlatTitlePane.this.activeChanged(true);
        FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
        if (FlatTitlePane.this.hasNativeCustomDecoration()) {
            FlatNativeWindowBorder.WindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
        }
        FlatTitlePane.this.repaintWindowBorder();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        FlatTitlePane.this.activeChanged(false);
        FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
        if (FlatTitlePane.this.hasNativeCustomDecoration()) {
            FlatNativeWindowBorder.WindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
        }
        FlatTitlePane.this.repaintWindowBorder();
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        FlatTitlePane.this.frameStateChanged();
        FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            if (e.getSource() == FlatTitlePane.this.iconLabel) {
                FlatTitlePane.this.close();
            } else if (!FlatTitlePane.this.hasNativeCustomDecoration() && FlatTitlePane.this.window instanceof Frame && ((Frame)FlatTitlePane.this.window).isResizable()) {
                Frame frame = (Frame)FlatTitlePane.this.window;
                if ((frame.getExtendedState() & 6) != 0) {
                    FlatTitlePane.this.restore();
                } else {
                    FlatTitlePane.this.maximize();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (FlatTitlePane.this.window == null) {
            return;
        }
        this.dragOffset = SwingUtilities.convertPoint(FlatTitlePane.this, e.getPoint(), FlatTitlePane.this.window);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Frame frame;
        int state;
        if (FlatTitlePane.this.window == null) {
            return;
        }
        if (FlatTitlePane.this.hasNativeCustomDecoration()) {
            return;
        }
        if (FlatTitlePane.this.window instanceof Frame && ((state = (frame = (Frame)FlatTitlePane.this.window).getExtendedState()) & 6) != 0) {
            int maximizedWidth = FlatTitlePane.this.window.getWidth();
            frame.setExtendedState(state & 0xFFFFFFF9);
            int restoredWidth = FlatTitlePane.this.window.getWidth();
            int center = restoredWidth / 2;
            if (this.dragOffset.x > center) {
                this.dragOffset.x = this.dragOffset.x > maximizedWidth - center ? restoredWidth - (maximizedWidth - this.dragOffset.x) : center;
            }
        }
        int newX = e.getXOnScreen() - this.dragOffset.x;
        int newY = e.getYOnScreen() - this.dragOffset.y;
        if (newX == FlatTitlePane.this.window.getX() && newY == FlatTitlePane.this.window.getY()) {
            return;
        }
        FlatTitlePane.this.window.setLocation(newX, newY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpotsLater();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        FlatTitlePane.this.frameStateChanged();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
