package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.UIManager;

public class FlatSearchIcon
extends FlatAbstractIcon {
    protected Color searchIconColor = UIManager.getColor("SearchField.searchIconColor");
    protected Color searchIconHoverColor = UIManager.getColor("SearchField.searchIconHoverColor");
    protected Color searchIconPressedColor = UIManager.getColor("SearchField.searchIconPressedColor");

    public FlatSearchIcon() {
        super(16, 16, null);
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.setColor(FlatButtonUI.buttonStateColor(c, this.searchIconColor, this.searchIconColor, null, this.searchIconHoverColor, this.searchIconPressedColor));
        Area area = new Area(new Ellipse2D.Float(2.0f, 2.0f, 10.0f, 10.0f));
        area.subtract(new Area(new Ellipse2D.Float(3.0f, 3.0f, 8.0f, 8.0f)));
        area.add(new Area(FlatUIUtils.createPath(10.813, 9.75, 14.0, 12.938, 12.938, 14.0, 9.75, 10.813)));
        g.fill(area);
    }
}
