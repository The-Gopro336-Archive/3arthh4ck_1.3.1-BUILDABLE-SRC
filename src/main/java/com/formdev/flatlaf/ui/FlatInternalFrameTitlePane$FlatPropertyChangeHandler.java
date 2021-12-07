package com.formdev.flatlaf.ui;

import java.beans.PropertyChangeEvent;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

class FlatInternalFrameTitlePane$FlatPropertyChangeHandler
extends BasicInternalFrameTitlePane.PropertyChangeHandler {
    protected FlatInternalFrameTitlePane$FlatPropertyChangeHandler() {
        super(FlatInternalFrameTitlePane.this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "title": {
                FlatInternalFrameTitlePane.this.titleLabel.setText(FlatInternalFrameTitlePane.this.frame.getTitle());
                break;
            }
            case "frameIcon": {
                FlatInternalFrameTitlePane.this.updateFrameIcon();
                break;
            }
            case "selected": {
                FlatInternalFrameTitlePane.this.updateColors();
                break;
            }
            case "iconable": 
            case "maximizable": 
            case "closable": {
                FlatInternalFrameTitlePane.this.updateButtonsVisibility();
                FlatInternalFrameTitlePane.this.enableActions();
                FlatInternalFrameTitlePane.this.revalidate();
                FlatInternalFrameTitlePane.this.repaint();
                return;
            }
            case "componentOrientation": {
                FlatInternalFrameTitlePane.this.applyComponentOrientation(FlatInternalFrameTitlePane.this.frame.getComponentOrientation());
            }
        }
        super.propertyChange(e);
    }
}
