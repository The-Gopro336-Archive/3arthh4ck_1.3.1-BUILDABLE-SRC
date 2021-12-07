package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatTextFieldUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class FlatPasswordFieldUI
extends FlatTextFieldUI {
    protected boolean showCapsLock;
    protected Icon capsLockIcon;
    private KeyListener capsLockListener;

    public static ComponentUI createUI(JComponent c) {
        return new FlatPasswordFieldUI();
    }

    @Override
    protected String getPropertyPrefix() {
        return "PasswordField";
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        String prefix = this.getPropertyPrefix();
        Character echoChar = (Character)UIManager.get(prefix + ".echoChar");
        if (echoChar != null) {
            LookAndFeel.installProperty(this.getComponent(), "echoChar", echoChar);
        }
        this.showCapsLock = UIManager.getBoolean("PasswordField.showCapsLock");
        this.capsLockIcon = UIManager.getIcon("PasswordField.capsLockIcon");
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.capsLockIcon = null;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.capsLockListener = new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent e) {
                this.repaint(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                this.repaint(e);
            }

            private void repaint(KeyEvent e) {
                if (e.getKeyCode() == 20) {
                    e.getComponent().repaint();
                    FlatPasswordFieldUI.this.scrollCaretToVisible();
                }
            }
        };
        this.getComponent().addKeyListener(this.capsLockListener);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.getComponent().removeKeyListener(this.capsLockListener);
        this.capsLockListener = null;
    }

    @Override
    protected void installKeyboardActions() {
        Action selectLineAction;
        super.installKeyboardActions();
        ActionMap map = SwingUtilities.getUIActionMap(this.getComponent());
        if (map != null && map.get("select-word") != null && (selectLineAction = map.get("select-line")) != null) {
            map.put("select-word", selectLineAction);
        }
    }

    @Override
    public View create(Element elem) {
        return new PasswordView(elem);
    }

    @Override
    protected void paintSafely(Graphics g) {
        Shape oldClip = g.getClip();
        super.paintSafely(g);
        g.setClip(oldClip);
        this.paintCapsLock(g);
    }

    protected void paintCapsLock(Graphics g) {
        if (!this.isCapsLockVisible()) {
            return;
        }
        JTextComponent c = this.getComponent();
        int y = (c.getHeight() - this.capsLockIcon.getIconHeight()) / 2;
        int x = c.getComponentOrientation().isLeftToRight() ? c.getWidth() - this.capsLockIcon.getIconWidth() - y : y;
        this.capsLockIcon.paintIcon(c, g, x, y);
    }

    protected boolean isCapsLockVisible() {
        if (!this.showCapsLock) {
            return false;
        }
        JTextComponent c = this.getComponent();
        return FlatUIUtils.isPermanentFocusOwner(c) && Toolkit.getDefaultToolkit().getLockingKeyState(20);
    }

    @Override
    protected Insets getPadding() {
        Insets padding = super.getPadding();
        if (!this.isCapsLockVisible()) {
            return padding;
        }
        boolean ltr = this.getComponent().getComponentOrientation().isLeftToRight();
        int iconWidth = this.capsLockIcon.getIconWidth();
        return FlatUIUtils.addInsets(padding, new Insets(0, ltr ? 0 : iconWidth, 0, ltr ? iconWidth : 0));
    }
}
