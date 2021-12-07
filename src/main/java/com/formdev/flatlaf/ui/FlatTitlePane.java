package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLabelUI;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.ui.FlatTitlePaneIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.JBRCustomDecorations;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public class FlatTitlePane
extends JComponent {
    protected final Color activeBackground = UIManager.getColor("TitlePane.background");
    protected final Color inactiveBackground = UIManager.getColor("TitlePane.inactiveBackground");
    protected final Color activeForeground = UIManager.getColor("TitlePane.foreground");
    protected final Color inactiveForeground = UIManager.getColor("TitlePane.inactiveForeground");
    protected final Color embeddedForeground = UIManager.getColor("TitlePane.embeddedForeground");
    protected final Color borderColor = UIManager.getColor("TitlePane.borderColor");
    protected final Dimension iconSize = UIManager.getDimension("TitlePane.iconSize");
    protected final int buttonMaximizedHeight = UIManager.getInt("TitlePane.buttonMaximizedHeight");
    protected final boolean centerTitle = UIManager.getBoolean("TitlePane.centerTitle");
    protected final boolean centerTitleIfMenuBarEmbedded = FlatUIUtils.getUIBoolean("TitlePane.centerTitleIfMenuBarEmbedded", true);
    protected final int menuBarTitleGap = FlatUIUtils.getUIInt("TitlePane.menuBarTitleGap", 20);
    protected final JRootPane rootPane;
    protected JPanel leftPanel;
    protected JLabel iconLabel;
    protected JComponent menuBarPlaceholder;
    protected JLabel titleLabel;
    protected JPanel buttonPanel;
    protected JButton iconifyButton;
    protected JButton maximizeButton;
    protected JButton restoreButton;
    protected JButton closeButton;
    protected Window window;
    private final Handler handler;
    private static final int HIT_TEST_SPOT_GROW = 2;

    public FlatTitlePane(JRootPane rootPane) {
        this.rootPane = rootPane;
        this.handler = this.createHandler();
        this.setBorder(this.createTitlePaneBorder());
        this.addSubComponents();
        this.activeChanged(true);
        this.addMouseListener(this.handler);
        this.addMouseMotionListener(this.handler);
        this.iconLabel.addMouseListener(this.handler);
    }

    protected FlatTitlePaneBorder createTitlePaneBorder() {
        return new FlatTitlePaneBorder();
    }

    protected Handler createHandler() {
        return new Handler();
    }

    protected void addSubComponents() {
        this.leftPanel = new JPanel();
        this.iconLabel = new JLabel();
        this.titleLabel = new JLabel(){

            @Override
            public void updateUI() {
                this.setUI(new FlatTitleLabelUI());
            }
        };
        this.iconLabel.setBorder(new FlatEmptyBorder(UIManager.getInsets("TitlePane.iconMargins")));
        this.titleLabel.setBorder(new FlatEmptyBorder(UIManager.getInsets("TitlePane.titleMargins")));
        this.titleLabel.setHorizontalAlignment(0);
        this.leftPanel.setLayout(new BoxLayout(this.leftPanel, 2));
        this.leftPanel.setOpaque(false);
        this.leftPanel.add(this.iconLabel);
        this.menuBarPlaceholder = new JComponent(){

            @Override
            public Dimension getPreferredSize() {
                JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
                return FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar) ? menuBar.getPreferredSize() : new Dimension();
            }
        };
        this.leftPanel.add(this.menuBarPlaceholder);
        this.createButtons();
        this.setLayout(new BorderLayout(){

            @Override
            public void layoutContainer(Container target) {
                Component horizontalGlue;
                JMenuBar menuBar;
                super.layoutContainer(target);
                Insets insets = target.getInsets();
                int width = target.getWidth() - insets.left - insets.right;
                if (FlatTitlePane.this.leftPanel.getWidth() + FlatTitlePane.this.buttonPanel.getWidth() > width) {
                    int oldWidth = FlatTitlePane.this.leftPanel.getWidth();
                    int newWidth = Math.max(width - FlatTitlePane.this.buttonPanel.getWidth(), 0);
                    FlatTitlePane.this.leftPanel.setSize(newWidth, FlatTitlePane.this.leftPanel.getHeight());
                    if (!FlatTitlePane.this.getComponentOrientation().isLeftToRight()) {
                        FlatTitlePane.this.leftPanel.setLocation(FlatTitlePane.this.leftPanel.getX() + (oldWidth - newWidth), FlatTitlePane.this.leftPanel.getY());
                    }
                }
                if (FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar = FlatTitlePane.this.rootPane.getJMenuBar()) && (horizontalGlue = FlatTitlePane.this.findHorizontalGlue(menuBar)) != null) {
                    Point glueLocation = SwingUtilities.convertPoint(horizontalGlue, 0, 0, FlatTitlePane.this.titleLabel);
                    FlatTitlePane.this.titleLabel.setBounds(FlatTitlePane.this.titleLabel.getX() + glueLocation.x, FlatTitlePane.this.titleLabel.getY(), horizontalGlue.getWidth(), FlatTitlePane.this.titleLabel.getHeight());
                }
            }
        });
        this.add((Component)this.leftPanel, "Before");
        this.add((Component)this.titleLabel, "Center");
        this.add((Component)this.buttonPanel, "After");
    }

    protected void createButtons() {
        this.iconifyButton = this.createButton("TitlePane.iconifyIcon", "Iconify", e -> this.iconify());
        this.maximizeButton = this.createButton("TitlePane.maximizeIcon", "Maximize", e -> this.maximize());
        this.restoreButton = this.createButton("TitlePane.restoreIcon", "Restore", e -> this.restore());
        this.closeButton = this.createButton("TitlePane.closeIcon", "Close", e -> this.close());
        this.buttonPanel = new JPanel(){

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                if (FlatTitlePane.this.buttonMaximizedHeight > 0 && FlatTitlePane.this.window instanceof Frame && (((Frame)FlatTitlePane.this.window).getExtendedState() & 6) != 0) {
                    size = new Dimension(size.width, Math.min(size.height, UIScale.scale(FlatTitlePane.this.buttonMaximizedHeight)));
                }
                return size;
            }
        };
        this.buttonPanel.setOpaque(false);
        this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, 2));
        if (this.rootPane.getWindowDecorationStyle() == 1) {
            this.restoreButton.setVisible(false);
            this.buttonPanel.add(this.iconifyButton);
            this.buttonPanel.add(this.maximizeButton);
            this.buttonPanel.add(this.restoreButton);
        }
        this.buttonPanel.add(this.closeButton);
    }

    protected JButton createButton(String iconKey, String accessibleName, ActionListener action) {
        JButton button = new JButton(UIManager.getIcon(iconKey));
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.putClientProperty("AccessibleName", accessibleName);
        button.addActionListener(action);
        return button;
    }

    protected void activeChanged(boolean active) {
        Color foreground;
        Color background = FlatClientProperties.clientPropertyColor(this.rootPane, "JRootPane.titleBarBackground", null);
        Color titleForeground = foreground = FlatClientProperties.clientPropertyColor(this.rootPane, "JRootPane.titleBarForeground", null);
        if (background == null) {
            background = FlatUIUtils.nonUIResource(active ? this.activeBackground : this.inactiveBackground);
        }
        if (foreground == null) {
            foreground = FlatUIUtils.nonUIResource(active ? this.activeForeground : this.inactiveForeground);
            titleForeground = active && this.hasVisibleEmbeddedMenuBar(this.rootPane.getJMenuBar()) ? FlatUIUtils.nonUIResource(this.embeddedForeground) : foreground;
        }
        this.setBackground(background);
        this.titleLabel.setForeground(titleForeground);
        this.iconifyButton.setForeground(foreground);
        this.maximizeButton.setForeground(foreground);
        this.restoreButton.setForeground(foreground);
        this.closeButton.setForeground(foreground);
        this.iconifyButton.setBackground(background);
        this.maximizeButton.setBackground(background);
        this.restoreButton.setBackground(background);
        this.closeButton.setBackground(background);
    }

    protected void frameStateChanged() {
        if (this.window == null || this.rootPane.getWindowDecorationStyle() != 1) {
            return;
        }
        if (this.window instanceof Frame) {
            Frame frame = (Frame)this.window;
            boolean resizable = frame.isResizable();
            boolean maximized = (frame.getExtendedState() & 6) != 0;
            this.iconifyButton.setVisible(true);
            this.maximizeButton.setVisible(resizable && !maximized);
            this.restoreButton.setVisible(resizable && maximized);
            if (maximized && this.rootPane.getClientProperty("_flatlaf.maximizedBoundsUpToDate") == null) {
                this.rootPane.putClientProperty("_flatlaf.maximizedBoundsUpToDate", null);
                Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
                this.updateMaximizedBounds();
                Rectangle newMaximizedBounds = frame.getMaximizedBounds();
                if (newMaximizedBounds != null && !newMaximizedBounds.equals(oldMaximizedBounds)) {
                    int oldExtendedState = frame.getExtendedState();
                    frame.setExtendedState(oldExtendedState & 0xFFFFFFF9);
                    frame.setExtendedState(oldExtendedState);
                }
            }
        } else {
            this.iconifyButton.setVisible(false);
            this.maximizeButton.setVisible(false);
            this.restoreButton.setVisible(false);
            this.revalidate();
            this.repaint();
        }
    }

    protected void updateIcon() {
        List<Image> images = this.window.getIconImages();
        if (images.isEmpty()) {
            for (Window owner = this.window.getOwner(); owner != null && (images = owner.getIconImages()).isEmpty(); owner = owner.getOwner()) {
            }
        }
        boolean hasIcon = true;
        if (!images.isEmpty()) {
            this.iconLabel.setIcon(new FlatTitlePaneIcon(images, this.iconSize));
        } else {
            Icon defaultIcon = UIManager.getIcon("TitlePane.icon");
            if (defaultIcon != null && (defaultIcon.getIconWidth() == 0 || defaultIcon.getIconHeight() == 0)) {
                defaultIcon = null;
            }
            if (defaultIcon != null) {
                if (defaultIcon instanceof ImageIcon) {
                    defaultIcon = new ScaledImageIcon((ImageIcon)defaultIcon, this.iconSize.width, this.iconSize.height);
                }
                this.iconLabel.setIcon(defaultIcon);
            } else {
                hasIcon = false;
            }
        }
        this.iconLabel.setVisible(hasIcon);
        this.updateNativeTitleBarHeightAndHitTestSpotsLater();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.uninstallWindowListeners();
        this.window = SwingUtilities.getWindowAncestor(this);
        if (this.window != null) {
            this.frameStateChanged();
            this.activeChanged(this.window.isActive());
            this.updateIcon();
            this.titleLabel.setText(this.getWindowTitle());
            this.installWindowListeners();
        }
        this.updateNativeTitleBarHeightAndHitTestSpotsLater();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.uninstallWindowListeners();
        this.window = null;
    }

    protected String getWindowTitle() {
        if (this.window instanceof Frame) {
            return ((Frame)this.window).getTitle();
        }
        if (this.window instanceof Dialog) {
            return ((Dialog)this.window).getTitle();
        }
        return null;
    }

    protected void installWindowListeners() {
        if (this.window == null) {
            return;
        }
        this.window.addPropertyChangeListener(this.handler);
        this.window.addWindowListener(this.handler);
        this.window.addWindowStateListener(this.handler);
        this.window.addComponentListener(this.handler);
    }

    protected void uninstallWindowListeners() {
        if (this.window == null) {
            return;
        }
        this.window.removePropertyChangeListener(this.handler);
        this.window.removeWindowListener(this.handler);
        this.window.removeWindowStateListener(this.handler);
        this.window.removeComponentListener(this.handler);
    }

    protected boolean hasVisibleEmbeddedMenuBar(JMenuBar menuBar) {
        return menuBar != null && menuBar.isVisible() && this.isMenuBarEmbedded();
    }

    protected boolean isMenuBarEmbedded() {
        return FlatUIUtils.getBoolean(this.rootPane, "flatlaf.menuBarEmbedded", "JRootPane.menuBarEmbedded", "TitlePane.menuBarEmbedded", false);
    }

    protected Rectangle getMenuBarBounds() {
        Insets insets = this.rootPane.getInsets();
        Rectangle bounds = new Rectangle(SwingUtilities.convertPoint(this.menuBarPlaceholder, -insets.left, -insets.top, this.rootPane), this.menuBarPlaceholder.getSize());
        Insets borderInsets = this.getBorder().getBorderInsets(this);
        bounds.height += borderInsets.bottom;
        Component horizontalGlue = this.findHorizontalGlue(this.rootPane.getJMenuBar());
        if (horizontalGlue != null) {
            boolean leftToRight = this.getComponentOrientation().isLeftToRight();
            int titleWidth = leftToRight ? this.buttonPanel.getX() - (this.leftPanel.getX() + this.leftPanel.getWidth()) : this.leftPanel.getX() - (this.buttonPanel.getX() + this.buttonPanel.getWidth());
            titleWidth = Math.max(titleWidth, 0);
            bounds.width += titleWidth;
            if (!leftToRight) {
                bounds.x -= titleWidth;
            }
        }
        return bounds;
    }

    protected Component findHorizontalGlue(JMenuBar menuBar) {
        if (menuBar == null) {
            return null;
        }
        int count = menuBar.getComponentCount();
        for (int i = count - 1; i >= 0; --i) {
            Component c = menuBar.getComponent(i);
            if (!(c instanceof Box.Filler) || c.getMaximumSize().width < Short.MAX_VALUE) continue;
            return c;
        }
        return null;
    }

    protected void titleBarColorsChanged() {
        this.activeChanged(this.window == null || this.window.isActive());
        this.repaint();
    }

    protected void menuBarChanged() {
        this.menuBarPlaceholder.invalidate();
        this.repaint();
        EventQueue.invokeLater(() -> this.activeChanged(this.window == null || this.window.isActive()));
    }

    protected void menuBarLayouted() {
        this.updateNativeTitleBarHeightAndHitTestSpotsLater();
        this.revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(UIManager.getBoolean("TitlePane.unifiedBackground") && FlatClientProperties.clientPropertyColor(this.rootPane, "JRootPane.titleBarBackground", null) == null ? FlatUIUtils.getParentBackground(this) : this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    protected void repaintWindowBorder() {
        int width = this.rootPane.getWidth();
        int height = this.rootPane.getHeight();
        Insets insets = this.rootPane.getInsets();
        this.rootPane.repaint(0, 0, width, insets.top);
        this.rootPane.repaint(0, 0, insets.left, height);
        this.rootPane.repaint(0, height - insets.bottom, width, insets.bottom);
        this.rootPane.repaint(width - insets.right, 0, insets.right, height);
    }

    protected void iconify() {
        if (!(this.window instanceof Frame)) {
            return;
        }
        Frame frame = (Frame)this.window;
        if (!FlatNativeWindowBorder.showWindow(this.window, 6)) {
            frame.setExtendedState(frame.getExtendedState() | 1);
        }
    }

    protected void maximize() {
        if (!(this.window instanceof Frame)) {
            return;
        }
        Frame frame = (Frame)this.window;
        this.updateMaximizedBounds();
        this.rootPane.putClientProperty("_flatlaf.maximizedBoundsUpToDate", true);
        if (!FlatNativeWindowBorder.showWindow(frame, 3)) {
            frame.setExtendedState(frame.getExtendedState() | 6);
        }
    }

    protected void updateMaximizedBounds() {
        Frame frame = (Frame)this.window;
        Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
        if (!this.hasNativeCustomDecoration() && (oldMaximizedBounds == null || Objects.equals(oldMaximizedBounds, this.rootPane.getClientProperty("_flatlaf.maximizedBounds")))) {
            GraphicsConfiguration gc = this.window.getGraphicsConfiguration();
            Rectangle screenBounds = gc.getBounds();
            int maximizedX = screenBounds.x;
            int maximizedY = screenBounds.y;
            int maximizedWidth = screenBounds.width;
            int maximizedHeight = screenBounds.height;
            if (!this.isMaximizedBoundsFixed()) {
                maximizedX = 0;
                maximizedY = 0;
                AffineTransform defaultTransform = gc.getDefaultTransform();
                maximizedWidth = (int)((double)maximizedWidth * defaultTransform.getScaleX());
                maximizedHeight = (int)((double)maximizedHeight * defaultTransform.getScaleY());
            }
            Insets screenInsets = this.window.getToolkit().getScreenInsets(gc);
            Rectangle newMaximizedBounds = new Rectangle(maximizedX + screenInsets.left, maximizedY + screenInsets.top, maximizedWidth - screenInsets.left - screenInsets.right, maximizedHeight - screenInsets.top - screenInsets.bottom);
            if (!Objects.equals(oldMaximizedBounds, newMaximizedBounds)) {
                frame.setMaximizedBounds(newMaximizedBounds);
                this.rootPane.putClientProperty("_flatlaf.maximizedBounds", newMaximizedBounds);
            }
        }
    }

    private boolean isMaximizedBoundsFixed() {
        return SystemInfo.isJava_15_orLater || SystemInfo.javaVersion >= SystemInfo.toVersion(11, 0, 8, 0) && SystemInfo.javaVersion < SystemInfo.toVersion(12, 0, 0, 0) || SystemInfo.javaVersion >= SystemInfo.toVersion(13, 0, 4, 0) && SystemInfo.javaVersion < SystemInfo.toVersion(14, 0, 0, 0);
    }

    protected void restore() {
        if (!(this.window instanceof Frame)) {
            return;
        }
        Frame frame = (Frame)this.window;
        if (!FlatNativeWindowBorder.showWindow(this.window, 9)) {
            int state = frame.getExtendedState();
            frame.setExtendedState((state & 1) != 0 ? state & 0xFFFFFFFE : state & 0xFFFFFFF9);
        }
    }

    protected void close() {
        if (this.window != null) {
            this.window.dispatchEvent(new WindowEvent(this.window, 201));
        }
    }

    private boolean hasJBRCustomDecoration() {
        return this.window != null && JBRCustomDecorations.hasCustomDecoration(this.window);
    }

    protected boolean hasNativeCustomDecoration() {
        return this.window != null && FlatNativeWindowBorder.hasCustomDecoration(this.window);
    }

    protected void updateNativeTitleBarHeightAndHitTestSpotsLater() {
        EventQueue.invokeLater(() -> this.updateNativeTitleBarHeightAndHitTestSpots());
    }

    protected void updateNativeTitleBarHeightAndHitTestSpots() {
        JMenuBar menuBar;
        Rectangle r;
        if (!this.isDisplayable()) {
            return;
        }
        if (!this.hasNativeCustomDecoration()) {
            return;
        }
        int titleBarHeight = this.getHeight();
        if (titleBarHeight > 0) {
            --titleBarHeight;
        }
        ArrayList<Rectangle> hitTestSpots = new ArrayList<Rectangle>();
        Rectangle appIconBounds = null;
        if (this.iconLabel.isVisible()) {
            Point location = SwingUtilities.convertPoint(this.iconLabel, 0, 0, this.window);
            Insets iconInsets = this.iconLabel.getInsets();
            Rectangle iconBounds = new Rectangle(location.x + iconInsets.left - 1, location.y + iconInsets.top - 1, this.iconLabel.getWidth() - iconInsets.left - iconInsets.right + 2, this.iconLabel.getHeight() - iconInsets.top - iconInsets.bottom + 2);
            if (this.window instanceof Frame && (((Frame)this.window).getExtendedState() & 6) != 0) {
                iconBounds.height += iconBounds.y;
                iconBounds.y = 0;
                if (this.window.getComponentOrientation().isLeftToRight()) {
                    iconBounds.width += iconBounds.x;
                    iconBounds.x = 0;
                } else {
                    iconBounds.width += iconInsets.right;
                }
            }
            if (this.hasJBRCustomDecoration()) {
                hitTestSpots.add(iconBounds);
            } else {
                appIconBounds = iconBounds;
            }
        }
        if ((r = this.getNativeHitTestSpot(this.buttonPanel)) != null) {
            hitTestSpots.add(r);
        }
        if (this.hasVisibleEmbeddedMenuBar(menuBar = this.rootPane.getJMenuBar()) && (r = this.getNativeHitTestSpot(this.menuBarPlaceholder)) != null) {
            Component horizontalGlue = this.findHorizontalGlue(menuBar);
            if (horizontalGlue != null) {
                Rectangle r2;
                Point glueLocation = SwingUtilities.convertPoint(horizontalGlue, 0, 0, this.window);
                if (this.getComponentOrientation().isLeftToRight()) {
                    int trailingWidth = r.x + r.width - 2 - glueLocation.x;
                    r.width -= trailingWidth;
                    r2 = new Rectangle(glueLocation.x + horizontalGlue.getWidth(), r.y, trailingWidth, r.height);
                } else {
                    int leadingWidth = glueLocation.x + horizontalGlue.getWidth() - (r.x + 2);
                    r.x += leadingWidth;
                    r.width -= leadingWidth;
                    r2 = new Rectangle(glueLocation.x - leadingWidth, r.y, leadingWidth, r.height);
                }
                r2.grow(2, 2);
                hitTestSpots.add(r2);
            }
            hitTestSpots.add(r);
        }
        FlatNativeWindowBorder.setTitleBarHeightAndHitTestSpots(this.window, titleBarHeight, hitTestSpots, appIconBounds);
    }

    protected Rectangle getNativeHitTestSpot(JComponent c) {
        Dimension size = c.getSize();
        if (size.width <= 0 || size.height <= 0) {
            return null;
        }
        Point location = SwingUtilities.convertPoint(c, 0, 0, this.window);
        Rectangle r = new Rectangle(location, size);
        r.grow(2, 2);
        return r;
    }

    protected class Handler
    extends WindowAdapter
    implements PropertyChangeListener,
    MouseListener,
    MouseMotionListener,
    ComponentListener {
        private Point dragOffset;

        protected Handler() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            switch (e.getPropertyName()) {
                case "title": {
                    FlatTitlePane.this.titleLabel.setText(FlatTitlePane.this.getWindowTitle());
                    break;
                }
                case "resizable": {
                    if (!(FlatTitlePane.this.window instanceof Frame)) break;
                    FlatTitlePane.this.frameStateChanged();
                    break;
                }
                case "iconImage": {
                    FlatTitlePane.this.updateIcon();
                    break;
                }
                case "componentOrientation": {
                    FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpotsLater();
                }
            }
        }

        @Override
        public void windowActivated(WindowEvent e) {
            FlatTitlePane.this.activeChanged(true);
            FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
            if (FlatTitlePane.this.hasNativeCustomDecoration()) {
                FlatNativeWindowBorder.WindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
            }
            FlatTitlePane.this.repaintWindowBorder();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            FlatTitlePane.this.activeChanged(false);
            FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
            if (FlatTitlePane.this.hasNativeCustomDecoration()) {
                FlatNativeWindowBorder.WindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
            }
            FlatTitlePane.this.repaintWindowBorder();
        }

        @Override
        public void windowStateChanged(WindowEvent e) {
            FlatTitlePane.this.frameStateChanged();
            FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpots();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                if (e.getSource() == FlatTitlePane.this.iconLabel) {
                    FlatTitlePane.this.close();
                } else if (!FlatTitlePane.this.hasNativeCustomDecoration() && FlatTitlePane.this.window instanceof Frame && ((Frame)FlatTitlePane.this.window).isResizable()) {
                    Frame frame = (Frame)FlatTitlePane.this.window;
                    if ((frame.getExtendedState() & 6) != 0) {
                        FlatTitlePane.this.restore();
                    } else {
                        FlatTitlePane.this.maximize();
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (FlatTitlePane.this.window == null) {
                return;
            }
            this.dragOffset = SwingUtilities.convertPoint(FlatTitlePane.this, e.getPoint(), FlatTitlePane.this.window);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Frame frame;
            int state;
            if (FlatTitlePane.this.window == null) {
                return;
            }
            if (FlatTitlePane.this.hasNativeCustomDecoration()) {
                return;
            }
            if (FlatTitlePane.this.window instanceof Frame && ((state = (frame = (Frame)FlatTitlePane.this.window).getExtendedState()) & 6) != 0) {
                int maximizedWidth = FlatTitlePane.this.window.getWidth();
                frame.setExtendedState(state & 0xFFFFFFF9);
                int restoredWidth = FlatTitlePane.this.window.getWidth();
                int center = restoredWidth / 2;
                if (this.dragOffset.x > center) {
                    this.dragOffset.x = this.dragOffset.x > maximizedWidth - center ? restoredWidth - (maximizedWidth - this.dragOffset.x) : center;
                }
            }
            int newX = e.getXOnScreen() - this.dragOffset.x;
            int newY = e.getYOnScreen() - this.dragOffset.y;
            if (newX == FlatTitlePane.this.window.getX() && newY == FlatTitlePane.this.window.getY()) {
                return;
            }
            FlatTitlePane.this.window.setLocation(newX, newY);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void componentResized(ComponentEvent e) {
            FlatTitlePane.this.updateNativeTitleBarHeightAndHitTestSpotsLater();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            FlatTitlePane.this.frameStateChanged();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }

    protected class FlatTitleLabelUI
    extends FlatLabelUI {
        protected FlatTitleLabelUI() {
        }

        @Override
        protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
            boolean center;
            boolean hasEmbeddedMenuBar = FlatTitlePane.this.hasVisibleEmbeddedMenuBar(FlatTitlePane.this.rootPane.getJMenuBar());
            int labelWidth = l.getWidth();
            int textWidth = labelWidth - textX * 2;
            int gap = UIScale.scale(FlatTitlePane.this.menuBarTitleGap);
            boolean bl = center = hasEmbeddedMenuBar ? FlatTitlePane.this.centerTitleIfMenuBarEmbedded : FlatTitlePane.this.centerTitle;
            if (center) {
                int centeredTextX = (l.getParent().getWidth() - textWidth) / 2 - l.getX();
                if (centeredTextX >= gap && centeredTextX + textWidth <= labelWidth - gap) {
                    textX = centeredTextX;
                }
            } else {
                int leadingTextX;
                boolean leftToRight = FlatTitlePane.this.getComponentOrientation().isLeftToRight();
                Insets insets = l.getInsets();
                int leadingInset = hasEmbeddedMenuBar ? gap : (leftToRight ? insets.left : insets.right);
                int n = leadingTextX = leftToRight ? leadingInset : labelWidth - leadingInset - textWidth;
                if (leftToRight ? leadingTextX < textX : leadingTextX > textX) {
                    textX = leadingTextX;
                }
            }
            super.paintEnabledText(l, g, s, textX, textY);
        }
    }

    protected class FlatTitlePaneBorder
    extends AbstractBorder {
        protected FlatTitlePaneBorder() {
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            super.getBorderInsets(c, insets);
            Border menuBarBorder = this.getMenuBarBorder();
            if (menuBarBorder != null) {
                Insets menuBarInsets = menuBarBorder.getBorderInsets(c);
                insets.bottom += menuBarInsets.bottom;
            } else if (!(FlatTitlePane.this.borderColor == null || FlatTitlePane.this.rootPane.getJMenuBar() != null && FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
                insets.bottom += UIScale.scale(1);
            }
            if (FlatTitlePane.this.hasNativeCustomDecoration() && !this.isWindowMaximized(c)) {
                insets = FlatUIUtils.addInsets(insets, FlatNativeWindowBorder.WindowTopBorder.getInstance().getBorderInsets());
            }
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Border menuBarBorder = this.getMenuBarBorder();
            if (menuBarBorder != null) {
                menuBarBorder.paintBorder(c, g, x, y, width, height);
            } else if (!(FlatTitlePane.this.borderColor == null || FlatTitlePane.this.rootPane.getJMenuBar() != null && FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
                float lineHeight = UIScale.scale(1.0f);
                FlatUIUtils.paintFilledRectangle(g, FlatTitlePane.this.borderColor, x, (float)(y + height) - lineHeight, width, lineHeight);
            }
            if (FlatTitlePane.this.hasNativeCustomDecoration() && !this.isWindowMaximized(c)) {
                FlatNativeWindowBorder.WindowTopBorder.getInstance().paintBorder(c, g, x, y, width, height);
            }
        }

        protected Border getMenuBarBorder() {
            JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
            return FlatTitlePane.this.hasVisibleEmbeddedMenuBar(menuBar) ? menuBar.getBorder() : null;
        }

        protected boolean isWindowMaximized(Component c) {
            return FlatTitlePane.this.window instanceof Frame ? (((Frame)FlatTitlePane.this.window).getExtendedState() & 6) != 0 : false;
        }
    }
}
