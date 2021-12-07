/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.ui.FlatRootPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class FlatMenuBarUI
extends BasicMenuBarUI {
    public static ComponentUI createUI(JComponent c) {
        return new FlatMenuBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.installProperty(this.menuBar, "opaque", false);
    }

    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        ActionMap map = SwingUtilities.getUIActionMap(this.menuBar);
        if (map == null) {
            map = new ActionMapUIResource();
            SwingUtilities.replaceUIActionMap(this.menuBar, map);
        }
        map.put("takeFocus", new TakeFocus());
    }

    @Override
    public void update(Graphics g, JComponent c) {
        Color background = this.getBackground(c);
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        this.paint(g, c);
    }

    protected Color getBackground(JComponent c) {
        Color background = c.getBackground();
        if (c.isOpaque() || !(background instanceof UIResource)) {
            return background;
        }
        JRootPane rootPane = SwingUtilities.getRootPane(c);
        if (rootPane == null || !(rootPane.getParent() instanceof Window) || rootPane.getJMenuBar() != c) {
            return background;
        }
        if (UIManager.getBoolean("TitlePane.unifiedBackground") && FlatNativeWindowBorder.hasCustomDecoration((Window)rootPane.getParent())) {
            background = FlatUIUtils.getParentBackground(c);
        }
        if (FlatUIUtils.isFullScreen(rootPane)) {
            return background;
        }
        return FlatRootPaneUI.isMenuBarEmbedded(rootPane) ? null : background;
    }

    private static class TakeFocus
    extends AbstractAction {
        private TakeFocus() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuBar menuBar = (JMenuBar)e.getSource();
            JMenu menu = menuBar.getMenu(0);
            if (menu != null) {
                MenuElement[] arrmenuElement;
                MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
                if (SystemInfo.isWindows) {
                    MenuElement[] arrmenuElement2 = new MenuElement[2];
                    arrmenuElement2[0] = menuBar;
                    arrmenuElement = arrmenuElement2;
                    arrmenuElement2[1] = menu;
                } else {
                    MenuElement[] arrmenuElement3 = new MenuElement[3];
                    arrmenuElement3[0] = menuBar;
                    arrmenuElement3[1] = menu;
                    arrmenuElement = arrmenuElement3;
                    arrmenuElement3[2] = menu.getPopupMenu();
                }
                menuSelectionManager.setSelectedPath(arrmenuElement);
                FlatLaf.showMnemonics(menuBar);
            }
        }
    }
}

