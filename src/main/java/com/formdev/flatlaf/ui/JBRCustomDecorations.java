/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;

public class JBRCustomDecorations {
    private static Boolean supported;
    private static Method Window_hasCustomDecoration;
    private static Method Window_setHasCustomDecoration;
    private static Method WWindowPeer_setCustomDecorationTitleBarHeight;
    private static Method WWindowPeer_setCustomDecorationHitTestSpots;
    private static Method AWTAccessor_getComponentAccessor;
    private static Method AWTAccessor_ComponentAccessor_getPeer;

    public static boolean isSupported() {
        JBRCustomDecorations.initialize();
        return supported;
    }

    static Object install(final JRootPane rootPane) {
        if (!JBRCustomDecorations.isSupported()) {
            return null;
        }
        Window window = SwingUtilities.windowForComponent(rootPane);
        if (window != null) {
            FlatNativeWindowBorder.install(window);
            return null;
        }
        HierarchyListener addListener = new HierarchyListener(){

            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if (e.getChanged() != rootPane || (e.getChangeFlags() & 1L) == 0L) {
                    return;
                }
                Container parent = e.getChangedParent();
                if (parent instanceof Window) {
                    FlatNativeWindowBorder.install((Window)parent);
                }
                EventQueue.invokeLater(() -> rootPane.removeHierarchyListener(this));
            }
        };
        rootPane.addHierarchyListener(addListener);
        return addListener;
    }

    static void uninstall(JRootPane rootPane, Object data) {
        Window window;
        if (data instanceof HierarchyListener) {
            rootPane.removeHierarchyListener((HierarchyListener)data);
        }
        if ((window = SwingUtilities.windowForComponent(rootPane)) != null) {
            JBRCustomDecorations.setHasCustomDecoration(window, false);
        }
    }

    static boolean hasCustomDecoration(Window window) {
        if (!JBRCustomDecorations.isSupported()) {
            return false;
        }
        try {
            return (Boolean)Window_hasCustomDecoration.invoke(window, new Object[0]);
        }
        catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
            return false;
        }
    }

    static void setHasCustomDecoration(Window window, boolean hasCustomDecoration) {
        if (!JBRCustomDecorations.isSupported()) {
            return;
        }
        try {
            if (hasCustomDecoration) {
                Window_setHasCustomDecoration.invoke(window, new Object[0]);
            } else {
                JBRCustomDecorations.setTitleBarHeightAndHitTestSpots(window, 4, Collections.emptyList());
            }
        }
        catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    static void setTitleBarHeightAndHitTestSpots(Window window, int titleBarHeight, List<Rectangle> hitTestSpots) {
        if (!JBRCustomDecorations.isSupported()) {
            return;
        }
        try {
            Object compAccessor = AWTAccessor_getComponentAccessor.invoke(null, new Object[0]);
            Object peer = AWTAccessor_ComponentAccessor_getPeer.invoke(compAccessor, window);
            WWindowPeer_setCustomDecorationTitleBarHeight.invoke(peer, titleBarHeight);
            WWindowPeer_setCustomDecorationHitTestSpots.invoke(peer, hitTestSpots);
        }
        catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    private static void initialize() {
        if (supported != null) {
            return;
        }
        supported = false;
        if (!SystemInfo.isJetBrainsJVM_11_orLater || !SystemInfo.isWindows_10_orLater) {
            return;
        }
        try {
            Class<?> awtAcessorClass = Class.forName("sun.awt.AWTAccessor");
            Class<?> compAccessorClass = Class.forName("sun.awt.AWTAccessor$ComponentAccessor");
            AWTAccessor_getComponentAccessor = awtAcessorClass.getDeclaredMethod("getComponentAccessor", new Class[0]);
            AWTAccessor_ComponentAccessor_getPeer = compAccessorClass.getDeclaredMethod("getPeer", Component.class);
            Class<?> peerClass = Class.forName("sun.awt.windows.WWindowPeer");
            WWindowPeer_setCustomDecorationTitleBarHeight = peerClass.getDeclaredMethod("setCustomDecorationTitleBarHeight", Integer.TYPE);
            WWindowPeer_setCustomDecorationHitTestSpots = peerClass.getDeclaredMethod("setCustomDecorationHitTestSpots", List.class);
            WWindowPeer_setCustomDecorationTitleBarHeight.setAccessible(true);
            WWindowPeer_setCustomDecorationHitTestSpots.setAccessible(true);
            Window_hasCustomDecoration = Window.class.getDeclaredMethod("hasCustomDecoration", new Class[0]);
            Window_setHasCustomDecoration = Window.class.getDeclaredMethod("setHasCustomDecoration", new Class[0]);
            Window_hasCustomDecoration.setAccessible(true);
            Window_setHasCustomDecoration.setAccessible(true);
            supported = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static class JBRWindowTopBorder
    extends BorderUIResource.EmptyBorderUIResource {
        private static JBRWindowTopBorder instance;
        private final Color defaultActiveBorder = new Color(0x707070);
        private final Color inactiveLightColor = new Color(0xAAAAAA);
        private boolean colorizationAffectsBorders;
        private Color activeColor = this.defaultActiveBorder;

        static JBRWindowTopBorder getInstance() {
            if (instance == null) {
                instance = new JBRWindowTopBorder();
            }
            return instance;
        }

        JBRWindowTopBorder() {
            super(1, 0, 0, 0);
            this.update();
            this.installListeners();
        }

        void update() {
            this.colorizationAffectsBorders = this.isColorizationColorAffectsBorders();
            this.activeColor = this.calculateActiveBorderColor();
        }

        void installListeners() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            toolkit.addPropertyChangeListener("win.dwm.colorizationColor.affects.borders", e -> {
                this.colorizationAffectsBorders = this.isColorizationColorAffectsBorders();
                this.activeColor = this.calculateActiveBorderColor();
            });
            PropertyChangeListener l = e -> {
                this.activeColor = this.calculateActiveBorderColor();
            };
            toolkit.addPropertyChangeListener("win.dwm.colorizationColor", l);
            toolkit.addPropertyChangeListener("win.dwm.colorizationColorBalance", l);
            toolkit.addPropertyChangeListener("win.frame.activeBorderColor", l);
        }

        boolean isColorizationColorAffectsBorders() {
            Object value = Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColor.affects.borders");
            return value instanceof Boolean ? (Boolean)value : true;
        }

        Color getColorizationColor() {
            return (Color)Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColor");
        }

        int getColorizationColorBalance() {
            Object value = Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColorBalance");
            return value instanceof Integer ? (Integer)value : -1;
        }

        private Color calculateActiveBorderColor() {
            if (!this.colorizationAffectsBorders) {
                return this.defaultActiveBorder;
            }
            Color colorizationColor = this.getColorizationColor();
            if (colorizationColor != null) {
                int colorizationColorBalance = this.getColorizationColorBalance();
                if (colorizationColorBalance < 0 || colorizationColorBalance > 100) {
                    colorizationColorBalance = 100;
                }
                if (colorizationColorBalance == 0) {
                    return new Color(0xD9D9D9);
                }
                if (colorizationColorBalance == 100) {
                    return colorizationColor;
                }
                float alpha = (float)colorizationColorBalance / 100.0f;
                float remainder = 1.0f - alpha;
                int r = Math.round((float)colorizationColor.getRed() * alpha + 217.0f * remainder);
                int g = Math.round((float)colorizationColor.getGreen() * alpha + 217.0f * remainder);
                int b = Math.round((float)colorizationColor.getBlue() * alpha + 217.0f * remainder);
                r = Math.min(Math.max(r, 0), 255);
                g = Math.min(Math.max(g, 0), 255);
                b = Math.min(Math.max(b, 0), 255);
                return new Color(r, g, b);
            }
            Color activeBorderColor = (Color)Toolkit.getDefaultToolkit().getDesktopProperty("win.frame.activeBorderColor");
            return activeBorderColor != null ? activeBorderColor : UIManager.getColor("MenuBar.borderColor");
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            boolean paintTopBorder;
            Window window = SwingUtilities.windowForComponent(c);
            boolean active = window != null ? window.isActive() : false;
            boolean bl = paintTopBorder = !FlatLaf.isLafDark() || active && this.colorizationAffectsBorders;
            if (!paintTopBorder) {
                return;
            }
            g.setColor(active ? this.activeColor : this.inactiveLightColor);
            HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, width, height, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> this.paintImpl(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5));
        }

        private void paintImpl(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
            g.drawRect(x, y, width - 1, 0);
        }

        void repaintBorder(Component c) {
            c.repaint(0, 0, c.getWidth(), 1);
        }
    }
}

