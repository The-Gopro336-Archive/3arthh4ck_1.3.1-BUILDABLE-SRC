/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.UIManager;

public class FlatClearIcon
extends FlatAbstractIcon {
    protected Color clearIconColor = UIManager.getColor("SearchField.clearIconColor");
    protected Color clearIconHoverColor = UIManager.getColor("SearchField.clearIconHoverColor");
    protected Color clearIconPressedColor = UIManager.getColor("SearchField.clearIconPressedColor");

    public FlatClearIcon() {
        super(16, 16, null);
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        ButtonModel model;
        if (c instanceof AbstractButton && ((model = ((AbstractButton)c).getModel()).isPressed() || model.isRollover())) {
            g.setColor(model.isPressed() ? this.clearIconPressedColor : this.clearIconHoverColor);
            Path2D.Float path = new Path2D.Float(0);
            path.append(new Ellipse2D.Float(1.75f, 1.75f, 12.5f, 12.5f), false);
            path.append(FlatUIUtils.createPath(4.5, 5.5, 5.5, 4.5, 8.0, 7.0, 10.5, 4.5, 11.5, 5.5, 9.0, 8.0, 11.5, 10.5, 10.5, 11.5, 8.0, 9.0, 5.5, 11.5, 4.5, 10.5, 7.0, 8.0), false);
            g.fill(path);
            return;
        }
        g.setColor(this.clearIconColor);
        Path2D.Float path = new Path2D.Float(0);
        path.append(new Line2D.Float(5.0f, 5.0f, 11.0f, 11.0f), false);
        path.append(new Line2D.Float(5.0f, 11.0f, 11.0f, 5.0f), false);
        g.draw(path);
    }
}

