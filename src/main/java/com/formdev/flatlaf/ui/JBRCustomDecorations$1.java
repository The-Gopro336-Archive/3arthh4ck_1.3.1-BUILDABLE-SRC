package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.JRootPane;

class JBRCustomDecorations$1
implements HierarchyListener {
    final JRootPane val$rootPane;

    JBRCustomDecorations$1(JRootPane jRootPane) {
        this.val$rootPane = jRootPane;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (e.getChanged() != this.val$rootPane || (e.getChangeFlags() & 1L) == 0L) {
            return;
        }
        Container parent = e.getChangedParent();
        if (parent instanceof Window) {
            FlatNativeWindowBorder.install((Window)parent);
        }
        EventQueue.invokeLater(() -> this.val$rootPane.removeHierarchyListener(this));
    }
}
