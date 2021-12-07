package com.formdev.flatlaf.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.plaf.UIResource;

class FlatTabbedPaneUI$ContainerUIResource
extends JPanel
implements UIResource {
    private FlatTabbedPaneUI$ContainerUIResource(Component c) {
        super(new BorderLayout());
        this.add(c);
    }
}
