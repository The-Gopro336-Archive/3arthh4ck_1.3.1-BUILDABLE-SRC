package com.formdev.flatlaf.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

class FlatDesktopPaneUI$LayoutDockListener
extends ComponentAdapter
implements ContainerListener {
    private FlatDesktopPaneUI$LayoutDockListener() {
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        FlatDesktopPaneUI.this.layoutDockLaterOnce();
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        FlatDesktopPaneUI.this.layoutDockLaterOnce();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        FlatDesktopPaneUI.this.layoutDockLaterOnce();
    }
}
