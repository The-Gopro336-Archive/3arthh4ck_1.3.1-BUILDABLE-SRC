package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Timer;

class FlatTabbedPaneUI$FlatWheelTabScroller
extends MouseAdapter {
    private int lastMouseX;
    private int lastMouseY;
    private boolean inViewport;
    private boolean scrolled;
    private Timer rolloverTimer;
    private Timer exitedTimer;
    private Animator animator;
    private Point startViewPosition;
    private Point targetViewPosition;

    protected FlatTabbedPaneUI$FlatWheelTabScroller() {
    }

    protected void uninstall() {
        if (this.rolloverTimer != null) {
            this.rolloverTimer.stop();
        }
        if (this.exitedTimer != null) {
            this.exitedTimer.stop();
        }
        if (this.animator != null) {
            this.animator.cancel();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (FlatTabbedPaneUI.this.tabPane.getMouseWheelListeners().length > 1) {
            return;
        }
        if (!this.isInViewport(e.getX(), e.getY())) {
            return;
        }
        this.lastMouseX = e.getX();
        this.lastMouseY = e.getY();
        double preciseWheelRotation = e.getPreciseWheelRotation();
        boolean isPreciseWheel = preciseWheelRotation != 0.0 && preciseWheelRotation != (double)e.getWheelRotation();
        int amount = (int)((double)FlatTabbedPaneUI.this.maxTabHeight * preciseWheelRotation);
        if (amount == 0) {
            if (preciseWheelRotation > 0.0) {
                amount = 1;
            } else if (preciseWheelRotation < 0.0) {
                amount = -1;
            }
        }
        Point viewPosition = this.targetViewPosition != null ? this.targetViewPosition : FlatTabbedPaneUI.this.tabViewport.getViewPosition();
        Dimension viewSize = FlatTabbedPaneUI.this.tabViewport.getViewSize();
        boolean horizontal = FlatTabbedPaneUI.this.isHorizontalTabPlacement();
        int x = viewPosition.x;
        int y = viewPosition.y;
        if (horizontal) {
            x += FlatTabbedPaneUI.this.isLeftToRight() ? amount : -amount;
        } else {
            y += amount;
        }
        if (isPreciseWheel && FlatTabbedPaneUI.this.getScrollButtonsPlacement() == 100 && FlatTabbedPaneUI.this.getScrollButtonsPolicy() == 3 && (FlatTabbedPaneUI.this.isLeftToRight() || !horizontal) || FlatTabbedPaneUI.this.scrollBackwardButtonPrefSize != null) {
            if (horizontal) {
                if (viewPosition.x == 0 && x > 0) {
                    x += ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).scrollBackwardButtonPrefSize.width;
                } else if (amount < 0 && x <= ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).scrollBackwardButtonPrefSize.width) {
                    x = 0;
                }
            } else if (viewPosition.y == 0 && y > 0) {
                y += ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).scrollBackwardButtonPrefSize.height;
            } else if (amount < 0 && y <= ((FlatTabbedPaneUI)FlatTabbedPaneUI.this).scrollBackwardButtonPrefSize.height) {
                y = 0;
            }
        }
        if (horizontal) {
            x = Math.min(Math.max(x, 0), viewSize.width - FlatTabbedPaneUI.this.tabViewport.getWidth());
        } else {
            y = Math.min(Math.max(y, 0), viewSize.height - FlatTabbedPaneUI.this.tabViewport.getHeight());
        }
        Point newViewPosition = new Point(x, y);
        if (newViewPosition.equals(viewPosition)) {
            return;
        }
        if (isPreciseWheel) {
            if (this.animator != null) {
                this.animator.stop();
            }
            FlatTabbedPaneUI.this.tabViewport.setViewPosition(newViewPosition);
            this.updateRolloverDelayed();
        } else {
            this.setViewPositionAnimated(newViewPosition);
        }
        this.scrolled = true;
    }

    protected void setViewPositionAnimated(Point viewPosition) {
        if (viewPosition.equals(FlatTabbedPaneUI.this.tabViewport.getViewPosition())) {
            return;
        }
        if (!FlatTabbedPaneUI.this.isSmoothScrollingEnabled()) {
            FlatTabbedPaneUI.this.tabViewport.setViewPosition(viewPosition);
            this.updateRolloverDelayed();
            return;
        }
        this.startViewPosition = FlatTabbedPaneUI.this.tabViewport.getViewPosition();
        this.targetViewPosition = viewPosition;
        if (this.animator == null) {
            int duration = 200;
            int resolution = 10;
            this.animator = new Animator(duration, fraction -> {
                if (FlatTabbedPaneUI.this.tabViewport == null || !FlatTabbedPaneUI.this.tabViewport.isShowing()) {
                    this.animator.stop();
                    return;
                }
                int x = this.startViewPosition.x + Math.round((float)(this.targetViewPosition.x - this.startViewPosition.x) * fraction);
                int y = this.startViewPosition.y + Math.round((float)(this.targetViewPosition.y - this.startViewPosition.y) * fraction);
                FlatTabbedPaneUI.this.tabViewport.setViewPosition(new Point(x, y));
            }, () -> {
                this.targetViewPosition = null;
                this.startViewPosition = null;
                if (FlatTabbedPaneUI.this.tabPane != null) {
                    FlatTabbedPaneUI.this.setRolloverTab(this.lastMouseX, this.lastMouseY);
                }
            });
            this.animator.setResolution(resolution);
            this.animator.setInterpolator(new CubicBezierEasing(0.5f, 0.5f, 0.5f, 1.0f));
        }
        this.animator.restart();
    }

    protected void updateRolloverDelayed() {
        int index;
        FlatTabbedPaneUI.this.blockRollover = true;
        int oldIndex = FlatTabbedPaneUI.this.getRolloverTab();
        if (oldIndex >= 0 && (index = FlatTabbedPaneUI.this.tabForCoordinate(FlatTabbedPaneUI.this.tabPane, this.lastMouseX, this.lastMouseY)) >= 0 && index != oldIndex) {
            FlatTabbedPaneUI.this.blockRollover = false;
            FlatTabbedPaneUI.this.setRolloverTab(-1);
            FlatTabbedPaneUI.this.blockRollover = true;
        }
        if (this.rolloverTimer == null) {
            this.rolloverTimer = new Timer(150, e -> {
                FlatTabbedPaneUI.this.blockRollover = false;
                if (FlatTabbedPaneUI.this.tabPane != null) {
                    FlatTabbedPaneUI.this.setRolloverTab(this.lastMouseX, this.lastMouseY);
                }
            });
            this.rolloverTimer.setRepeats(false);
        }
        this.rolloverTimer.restart();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.checkViewportExited(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.checkViewportExited(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        FlatTabbedPaneUI.this.setRolloverTab(e.getX(), e.getY());
    }

    protected boolean isInViewport(int x, int y) {
        return FlatTabbedPaneUI.this.tabViewport != null && FlatTabbedPaneUI.this.tabViewport.getBounds().contains(x, y);
    }

    protected void checkViewportExited(int x, int y) {
        this.lastMouseX = x;
        this.lastMouseY = y;
        boolean wasInViewport = this.inViewport;
        this.inViewport = this.isInViewport(x, y);
        if (this.inViewport != wasInViewport) {
            if (!this.inViewport) {
                this.viewportExited();
            } else if (this.exitedTimer != null) {
                this.exitedTimer.stop();
            }
        }
    }

    protected void viewportExited() {
        if (!this.scrolled) {
            return;
        }
        if (this.exitedTimer == null) {
            this.exitedTimer = new Timer(500, e -> this.ensureSelectedTabVisible());
            this.exitedTimer.setRepeats(false);
        }
        this.exitedTimer.start();
    }

    protected void ensureSelectedTabVisible() {
        if (FlatTabbedPaneUI.this.tabPane == null || FlatTabbedPaneUI.this.tabViewport == null) {
            return;
        }
        if (!this.scrolled || FlatTabbedPaneUI.this.tabViewport == null) {
            return;
        }
        this.scrolled = false;
        FlatTabbedPaneUI.this.ensureSelectedTabIsVisible();
    }
}
