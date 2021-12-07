package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.FlatWindowsNativeWindowBorder;
import com.formdev.flatlaf.ui.JBRCustomDecorations;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

public class FlatNativeWindowBorder {
    private static final boolean canUseWindowDecorations = SystemInfo.isWindows_10_orLater && !SystemInfo.isProjector && !SystemInfo.isWebswing && !SystemInfo.isWinPE && FlatSystemProperties.getBoolean("flatlaf.useWindowDecorations", true);
    private static final boolean canUseJBRCustomDecorations = canUseWindowDecorations && SystemInfo.isJetBrainsJVM_11_orLater && FlatSystemProperties.getBoolean("flatlaf.useJetBrainsCustomDecorations", true);
    private static Boolean supported;
    private static Provider nativeProvider;

    public static boolean isSupported() {
        if (canUseJBRCustomDecorations) {
            return JBRCustomDecorations.isSupported();
        }
        FlatNativeWindowBorder.initialize();
        return supported;
    }

    static Object install(JRootPane rootPane) {
        if (canUseJBRCustomDecorations) {
            return JBRCustomDecorations.install(rootPane);
        }
        if (!FlatNativeWindowBorder.isSupported()) {
            return null;
        }
        Window window = SwingUtilities.windowForComponent(rootPane);
        if (window != null && window.isDisplayable()) {
            FlatNativeWindowBorder.install(window);
        }
        PropertyChangeListener ancestorListener = e -> {
            Object newValue = e.getNewValue();
            if (newValue instanceof Window) {
                FlatNativeWindowBorder.install((Window)newValue);
            } else if (newValue == null && e.getOldValue() instanceof Window) {
                FlatNativeWindowBorder.uninstall((Window)e.getOldValue());
            }
        };
        rootPane.addPropertyChangeListener("ancestor", ancestorListener);
        return ancestorListener;
    }

    static void install(Window window) {
        if (FlatNativeWindowBorder.hasCustomDecoration(window)) {
            return;
        }
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            return;
        }
        if (window instanceof JFrame) {
            JFrame frame = (JFrame)window;
            JRootPane rootPane = frame.getRootPane();
            if (!FlatNativeWindowBorder.useWindowDecorations(rootPane)) {
                return;
            }
            if (frame.isUndecorated()) {
                return;
            }
            FlatNativeWindowBorder.setHasCustomDecoration(frame, true);
            if (!FlatNativeWindowBorder.hasCustomDecoration(frame)) {
                return;
            }
            rootPane.setWindowDecorationStyle(1);
        } else if (window instanceof JDialog) {
            JDialog dialog = (JDialog)window;
            JRootPane rootPane = dialog.getRootPane();
            if (!FlatNativeWindowBorder.useWindowDecorations(rootPane)) {
                return;
            }
            if (dialog.isUndecorated()) {
                return;
            }
            FlatNativeWindowBorder.setHasCustomDecoration(dialog, true);
            if (!FlatNativeWindowBorder.hasCustomDecoration(dialog)) {
                return;
            }
            rootPane.setWindowDecorationStyle(2);
        }
    }

    static void uninstall(JRootPane rootPane, Object data) {
        if (canUseJBRCustomDecorations) {
            JBRCustomDecorations.uninstall(rootPane, data);
            return;
        }
        if (!FlatNativeWindowBorder.isSupported()) {
            return;
        }
        if (data instanceof PropertyChangeListener) {
            rootPane.removePropertyChangeListener("ancestor", (PropertyChangeListener)data);
        }
        if (UIManager.getLookAndFeel() instanceof FlatLaf && FlatNativeWindowBorder.useWindowDecorations(rootPane)) {
            return;
        }
        Window window = SwingUtilities.windowForComponent(rootPane);
        if (window != null) {
            FlatNativeWindowBorder.uninstall(window);
        }
    }

    private static void uninstall(Window window) {
        if (!FlatNativeWindowBorder.hasCustomDecoration(window)) {
            return;
        }
        FlatNativeWindowBorder.setHasCustomDecoration(window, false);
        if (window instanceof JFrame) {
            JFrame frame = (JFrame)window;
            frame.getRootPane().setWindowDecorationStyle(0);
        } else if (window instanceof JDialog) {
            JDialog dialog = (JDialog)window;
            dialog.getRootPane().setWindowDecorationStyle(0);
        }
    }

    private static boolean useWindowDecorations(JRootPane rootPane) {
        return FlatUIUtils.getBoolean(rootPane, "flatlaf.useWindowDecorations", "JRootPane.useWindowDecorations", "TitlePane.useWindowDecorations", false);
    }

    public static boolean hasCustomDecoration(Window window) {
        if (canUseJBRCustomDecorations) {
            return JBRCustomDecorations.hasCustomDecoration(window);
        }
        if (!FlatNativeWindowBorder.isSupported()) {
            return false;
        }
        return nativeProvider.hasCustomDecoration(window);
    }

    public static void setHasCustomDecoration(Window window, boolean hasCustomDecoration) {
        if (canUseJBRCustomDecorations) {
            JBRCustomDecorations.setHasCustomDecoration(window, hasCustomDecoration);
            return;
        }
        if (!FlatNativeWindowBorder.isSupported()) {
            return;
        }
        nativeProvider.setHasCustomDecoration(window, hasCustomDecoration);
    }

    static void setTitleBarHeightAndHitTestSpots(Window window, int titleBarHeight, List<Rectangle> hitTestSpots, Rectangle appIconBounds) {
        if (canUseJBRCustomDecorations) {
            JBRCustomDecorations.setTitleBarHeightAndHitTestSpots(window, titleBarHeight, hitTestSpots);
            return;
        }
        if (!FlatNativeWindowBorder.isSupported()) {
            return;
        }
        nativeProvider.setTitleBarHeight(window, titleBarHeight);
        nativeProvider.setTitleBarHitTestSpots(window, hitTestSpots);
        nativeProvider.setTitleBarAppIconBounds(window, appIconBounds);
    }

    static boolean showWindow(Window window, int cmd) {
        if (canUseJBRCustomDecorations || !FlatNativeWindowBorder.isSupported()) {
            return false;
        }
        return nativeProvider.showWindow(window, cmd);
    }

    private static void initialize() {
        if (supported != null) {
            return;
        }
        supported = false;
        if (!canUseWindowDecorations) {
            return;
        }
        try {
            FlatNativeWindowBorder.setNativeProvider(FlatWindowsNativeWindowBorder.getInstance());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void setNativeProvider(Provider provider) {
        if (nativeProvider != null) {
            throw new IllegalStateException();
        }
        nativeProvider = provider;
        supported = nativeProvider != null;
    }

    static class WindowTopBorder
    extends JBRCustomDecorations.JBRWindowTopBorder {
        private static WindowTopBorder instance;

        WindowTopBorder() {
        }

        static JBRCustomDecorations.JBRWindowTopBorder getInstance() {
            if (canUseJBRCustomDecorations) {
                return JBRCustomDecorations.JBRWindowTopBorder.getInstance();
            }
            if (instance == null) {
                instance = new WindowTopBorder();
            }
            return instance;
        }

        @Override
        void installListeners() {
            nativeProvider.addChangeListener(e -> {
                this.update();
                for (Window window : Window.getWindows()) {
                    if (!window.isDisplayable()) continue;
                    window.repaint(0, 0, window.getWidth(), 1);
                }
            });
        }

        @Override
        boolean isColorizationColorAffectsBorders() {
            return nativeProvider.isColorizationColorAffectsBorders();
        }

        @Override
        Color getColorizationColor() {
            return nativeProvider.getColorizationColor();
        }

        @Override
        int getColorizationColorBalance() {
            return nativeProvider.getColorizationColorBalance();
        }
    }

    public static interface Provider {
        public static final int SW_MAXIMIZE = 3;
        public static final int SW_MINIMIZE = 6;
        public static final int SW_RESTORE = 9;

        public boolean hasCustomDecoration(Window var1);

        public void setHasCustomDecoration(Window var1, boolean var2);

        public void setTitleBarHeight(Window var1, int var2);

        public void setTitleBarHitTestSpots(Window var1, List<Rectangle> var2);

        public void setTitleBarAppIconBounds(Window var1, Rectangle var2);

        public boolean showWindow(Window var1, int var2);

        public boolean isColorizationColorAffectsBorders();

        public Color getColorizationColor();

        public int getColorizationColorBalance();

        public void addChangeListener(ChangeListener var1);

        public void removeChangeListener(ChangeListener var1);
    }
}
