package com.formdev.flatlaf.ui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class FlatComboBoxUI$FlatComboPopup$PopupListCellRenderer
implements ListCellRenderer {
    private FlatComboBoxUI$FlatComboPopup$PopupListCellRenderer() {
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        FlatComboPopup.this.this$0.paddingBorder.uninstall();
        DefaultListCellRenderer renderer = FlatComboPopup.this.comboBox.getRenderer();
        if (renderer == null) {
            renderer = new DefaultListCellRenderer();
        }
        Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        c.applyComponentOrientation(FlatComboPopup.this.comboBox.getComponentOrientation());
        FlatComboPopup.this.this$0.paddingBorder.install(c);
        return c;
    }
}
