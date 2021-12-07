package com.formdev.flatlaf.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class FlatScrollBarUI$ScrollBarHoverListener
extends MouseAdapter {
    private FlatScrollBarUI$ScrollBarHoverListener() {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!isPressed) {
            FlatScrollBarUI.this.hoverThumb = false;
            FlatScrollBarUI.this.hoverTrack = false;
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isPressed) {
            this.update(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isPressed = true;
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isPressed = false;
        this.repaint();
        this.update(e.getX(), e.getY());
    }

    private void update(int x, int y) {
        boolean inTrack = FlatScrollBarUI.this.getTrackBounds().contains(x, y);
        boolean inThumb = FlatScrollBarUI.this.getThumbBounds().contains(x, y);
        if (inTrack != FlatScrollBarUI.this.hoverTrack || inThumb != FlatScrollBarUI.this.hoverThumb) {
            FlatScrollBarUI.this.hoverTrack = inTrack;
            FlatScrollBarUI.this.hoverThumb = inThumb;
            this.repaint();
        }
    }

    private void repaint() {
        if (FlatScrollBarUI.this.scrollbar.isEnabled()) {
            FlatScrollBarUI.this.scrollbar.repaint();
        }
    }
}
