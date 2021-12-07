package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.UIScale;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

class UIScale$1
implements PropertyChangeListener {
    UIScale$1() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case "lookAndFeel": {
                if (e.getNewValue() instanceof LookAndFeel) {
                    UIManager.getLookAndFeelDefaults().addPropertyChangeListener(this);
                }
                UIScale.updateScaleFactor();
                break;
            }
            case "defaultFont": 
            case "Label.font": {
                UIScale.updateScaleFactor();
            }
        }
    }
}
