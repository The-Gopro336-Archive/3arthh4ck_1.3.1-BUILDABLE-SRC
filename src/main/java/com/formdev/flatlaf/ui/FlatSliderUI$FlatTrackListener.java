package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatSliderUI;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSliderUI;

class FlatSliderUI$FlatTrackListener
extends BasicSliderUI.TrackListener {
    protected FlatSliderUI$FlatTrackListener() {
        super(FlatSliderUI.this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setThumbHover(this.isOverThumb(e));
        super.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setThumbHover(false);
        super.mouseExited(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.setThumbHover(this.isOverThumb(e));
        super.mouseMoved(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.setThumbPressed(this.isOverThumb(e));
        if (!FlatSliderUI.this.slider.isEnabled()) {
            return;
        }
        if (UIManager.getBoolean("Slider.scrollOnTrackClick")) {
            super.mousePressed(e);
            return;
        }
        int x = e.getX();
        int y = e.getY();
        FlatSliderUI.this.calculateGeometry();
        if (FlatSliderUI.this.thumbRect.contains(x, y)) {
            super.mousePressed(e);
            return;
        }
        if (UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag") && !SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        int tx = ((FlatSliderUI)FlatSliderUI.this).thumbRect.x + ((FlatSliderUI)FlatSliderUI.this).thumbRect.width / 2 - x;
        int ty = ((FlatSliderUI)FlatSliderUI.this).thumbRect.y + ((FlatSliderUI)FlatSliderUI.this).thumbRect.height / 2 - y;
        e.translatePoint(tx, ty);
        super.mousePressed(e);
        e.translatePoint(-tx, -ty);
        this.mouseDragged(e);
        this.setThumbPressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.setThumbPressed(false);
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (FlatSliderUI.this.isDragging() && FlatSliderUI.this.slider.getSnapToTicks() && FlatSliderUI.this.slider.isEnabled() && !UIManager.getBoolean("Slider.snapToTicksOnReleased")) {
            FlatSliderUI.this.calculateThumbLocation();
            FlatSliderUI.this.slider.repaint();
        }
    }

    protected void setThumbHover(boolean hover) {
        if (hover != FlatSliderUI.this.thumbHover) {
            FlatSliderUI.this.thumbHover = hover;
            FlatSliderUI.this.slider.repaint(FlatSliderUI.this.thumbRect);
        }
    }

    protected void setThumbPressed(boolean pressed) {
        if (pressed != FlatSliderUI.this.thumbPressed) {
            FlatSliderUI.this.thumbPressed = pressed;
            FlatSliderUI.this.slider.repaint(FlatSliderUI.this.thumbRect);
        }
    }

    protected boolean isOverThumb(MouseEvent e) {
        return e != null && FlatSliderUI.this.slider.isEnabled() && FlatSliderUI.this.thumbRect.contains(e.getX(), e.getY());
    }
}
