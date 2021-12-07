package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatWindowResizer;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.function.Supplier;
import javax.swing.DesktopManager;
import javax.swing.JInternalFrame;

public class FlatWindowResizer$InternalFrameResizer
extends FlatWindowResizer {
    protected final Supplier<DesktopManager> desktopManager;

    public FlatWindowResizer$InternalFrameResizer(JInternalFrame frame, Supplier<DesktopManager> desktopManager) {
        super(frame);
        this.desktopManager = desktopManager;
        frame.addPropertyChangeListener("resizable", this);
    }

    @Override
    public void uninstall() {
        this.getFrame().removePropertyChangeListener("resizable", this);
        super.uninstall();
    }

    private JInternalFrame getFrame() {
        return (JInternalFrame)this.resizeComp;
    }

    @Override
    protected Insets getResizeInsets() {
        return this.getFrame().getInsets();
    }

    @Override
    protected boolean isWindowResizable() {
        return this.getFrame().isResizable();
    }

    @Override
    protected Rectangle getWindowBounds() {
        return this.getFrame().getBounds();
    }

    @Override
    protected void setWindowBounds(Rectangle r) {
        this.desktopManager.get().resizeFrame(this.getFrame(), r.x, r.y, r.width, r.height);
    }

    @Override
    protected boolean limitToParentBounds() {
        return true;
    }

    @Override
    protected Rectangle getParentBounds() {
        return this.getFrame().getParent().getBounds();
    }

    @Override
    protected boolean honorMinimumSizeOnResize() {
        return true;
    }

    @Override
    protected boolean honorMaximumSizeOnResize() {
        return true;
    }

    @Override
    protected Dimension getWindowMinimumSize() {
        return this.getFrame().getMinimumSize();
    }

    @Override
    protected Dimension getWindowMaximumSize() {
        return this.getFrame().getMaximumSize();
    }

    @Override
    protected void beginResizing(int direction) {
        this.desktopManager.get().beginResizingFrame(this.getFrame(), direction);
    }

    @Override
    protected void endResizing() {
        this.desktopManager.get().endResizingFrame(this.getFrame());
    }
}
