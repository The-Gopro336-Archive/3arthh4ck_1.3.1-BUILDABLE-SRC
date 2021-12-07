package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.FlatWindowResizer;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.JRootPane;

public class FlatWindowResizer$WindowResizer
extends FlatWindowResizer
implements WindowStateListener {
    protected Window window;

    public FlatWindowResizer$WindowResizer(JRootPane rootPane) {
        super(rootPane);
    }

    @Override
    protected void addNotify() {
        Container parent = this.resizeComp.getParent();
        Window window = this.window = parent instanceof Window ? (Window)parent : null;
        if (this.window instanceof Frame) {
            this.window.addPropertyChangeListener("resizable", this);
            this.window.addWindowStateListener(this);
        }
        super.addNotify();
    }

    @Override
    protected void removeNotify() {
        if (this.window instanceof Frame) {
            this.window.removePropertyChangeListener("resizable", this);
            this.window.removeWindowStateListener(this);
        }
        this.window = null;
        super.removeNotify();
    }

    @Override
    protected boolean isWindowResizable() {
        if (FlatUIUtils.isFullScreen(this.resizeComp)) {
            return false;
        }
        if (this.window instanceof Frame) {
            return ((Frame)this.window).isResizable() && (((Frame)this.window).getExtendedState() & 6) == 0;
        }
        if (this.window instanceof Dialog) {
            return ((Dialog)this.window).isResizable();
        }
        return false;
    }

    @Override
    protected Rectangle getWindowBounds() {
        return this.window.getBounds();
    }

    @Override
    protected void setWindowBounds(Rectangle r) {
        this.window.setBounds(r);
        this.doLayout();
        if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
            this.window.validate();
            this.resizeComp.repaint();
        }
    }

    @Override
    protected boolean limitToParentBounds() {
        return false;
    }

    @Override
    protected Rectangle getParentBounds() {
        return null;
    }

    @Override
    protected boolean honorMinimumSizeOnResize() {
        return this.honorFrameMinimumSizeOnResize && this.window instanceof Frame || this.honorDialogMinimumSizeOnResize && this.window instanceof Dialog;
    }

    @Override
    protected boolean honorMaximumSizeOnResize() {
        return false;
    }

    @Override
    protected Dimension getWindowMinimumSize() {
        return this.window.getMinimumSize();
    }

    @Override
    protected Dimension getWindowMaximumSize() {
        return this.window.getMaximumSize();
    }

    @Override
    boolean isDialog() {
        return this.window instanceof Dialog;
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        this.updateVisibility();
    }
}
