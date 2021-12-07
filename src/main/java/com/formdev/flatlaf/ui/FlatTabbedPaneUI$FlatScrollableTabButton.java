package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class FlatTabbedPaneUI$FlatScrollableTabButton
extends FlatTabbedPaneUI.FlatTabAreaButton
implements MouseListener {
    private Timer autoRepeatTimer;

    protected FlatTabbedPaneUI$FlatScrollableTabButton(int direction) {
        super(FlatTabbedPaneUI.this, direction);
        this.addMouseListener(this);
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        FlatTabbedPaneUI.this.runWithOriginalLayoutManager(() -> super.fireActionPerformed(event));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && this.isEnabled()) {
            if (this.autoRepeatTimer == null) {
                this.autoRepeatTimer = new Timer(60, e2 -> {
                    if (this.isEnabled()) {
                        this.doClick();
                    }
                });
                this.autoRepeatTimer.setInitialDelay(300);
            }
            this.autoRepeatTimer.start();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.autoRepeatTimer != null) {
            this.autoRepeatTimer.stop();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (this.autoRepeatTimer != null && this.isPressed()) {
            this.autoRepeatTimer.start();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (this.autoRepeatTimer != null) {
            this.autoRepeatTimer.stop();
        }
    }
}
