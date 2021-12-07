package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

class FlatTabbedPaneUI$RunWithOriginalLayoutManagerDelegateAction
implements Action {
    private final Action delegate;

    static void install(ActionMap map, String key) {
        Action oldAction = map.get(key);
        if (oldAction == null || oldAction instanceof FlatTabbedPaneUI$RunWithOriginalLayoutManagerDelegateAction) {
            return;
        }
        map.put(key, new FlatTabbedPaneUI$RunWithOriginalLayoutManagerDelegateAction(oldAction));
    }

    private FlatTabbedPaneUI$RunWithOriginalLayoutManagerDelegateAction(Action delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object getValue(String key) {
        return this.delegate.getValue(key);
    }

    @Override
    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    @Override
    public void putValue(String key, Object value) {
    }

    @Override
    public void setEnabled(boolean b) {
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
        TabbedPaneUI ui = tabbedPane.getUI();
        if (ui instanceof FlatTabbedPaneUI) {
            ((FlatTabbedPaneUI)ui).runWithOriginalLayoutManager(() -> this.delegate.actionPerformed(e));
        } else {
            this.delegate.actionPerformed(e);
        }
    }
}
