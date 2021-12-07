package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

public class FlatArrowButton
extends BasicArrowButton
implements UIResource {
    public static final int DEFAULT_ARROW_WIDTH = 8;
    protected final boolean chevron;
    protected final Color foreground;
    protected final Color disabledForeground;
    protected final Color hoverForeground;
    protected final Color hoverBackground;
    protected final Color pressedForeground;
    protected final Color pressedBackground;
    private int arrowWidth = 8;
    private float xOffset = 0.0f;
    private float yOffset = 0.0f;
    private boolean hover;
    private boolean pressed;

    public FlatArrowButton(int direction, String type, Color foreground, Color disabledForeground, Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground) {
        super(direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
        this.chevron = FlatUIUtils.isChevron(type);
        this.foreground = foreground;
        this.disabledForeground = disabledForeground;
        this.hoverForeground = hoverForeground;
        this.hoverBackground = hoverBackground;
        this.pressedForeground = pressedForeground;
        this.pressedBackground = pressedBackground;
        this.setOpaque(false);
        this.setBorder(null);
        if (hoverForeground != null || hoverBackground != null || pressedForeground != null || pressedBackground != null) {
            this.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseEntered(MouseEvent e) {
                    FlatArrowButton.this.hover = true;
                    FlatArrowButton.this.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    FlatArrowButton.this.hover = false;
                    FlatArrowButton.this.repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    FlatArrowButton.this.pressed = true;
                    FlatArrowButton.this.repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    FlatArrowButton.this.pressed = false;
                    FlatArrowButton.this.repaint();
                }
            });
        }
    }

    public int getArrowWidth() {
        return this.arrowWidth;
    }

    public void setArrowWidth(int arrowWidth) {
        this.arrowWidth = arrowWidth;
    }

    protected boolean isHover() {
        return this.hover;
    }

    protected boolean isPressed() {
        return this.pressed;
    }

    public float getXOffset() {
        return this.xOffset;
    }

    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    protected Color deriveBackground(Color background) {
        return background;
    }

    protected Color deriveForeground(Color foreground) {
        return FlatUIUtils.deriveColor(foreground, this.foreground);
    }

    protected Color getArrowColor() {
        return this.isEnabled() ? (this.pressedForeground != null && this.isPressed() ? this.pressedForeground : (this.hoverForeground != null && this.isHover() ? this.hoverForeground : this.foreground)) : this.disabledForeground;
    }

    @Override
    public Dimension getPreferredSize() {
        return UIScale.scale(super.getPreferredSize());
    }

    @Override
    public Dimension getMinimumSize() {
        return UIScale.scale(super.getMinimumSize());
    }

    @Override
    public void paint(Graphics g) {
        Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g);
        if (this.isEnabled()) {
            Color background;
            Color color = this.pressedBackground != null && this.isPressed() ? this.pressedBackground : (background = this.hoverBackground != null && this.isHover() ? this.hoverBackground : null);
            if (background != null) {
                g.setColor(this.deriveBackground(background));
                this.paintBackground((Graphics2D)g);
            }
        }
        g.setColor(this.deriveForeground(this.getArrowColor()));
        this.paintArrow((Graphics2D)g);
        FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
    }

    protected void paintBackground(Graphics2D g) {
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    protected void paintArrow(Graphics2D g) {
        boolean vert = this.direction == 1 || this.direction == 5;
        int x = 0;
        Container parent = this.getParent();
        if (vert && parent instanceof JComponent && FlatUIUtils.hasRoundBorder((JComponent)parent)) {
            x -= UIScale.scale(parent.getComponentOrientation().isLeftToRight() ? 1 : -1);
        }
        FlatUIUtils.paintArrow(g, x, 0, this.getWidth(), this.getHeight(), this.getDirection(), this.chevron, this.arrowWidth, this.xOffset, this.yOffset);
    }
}
