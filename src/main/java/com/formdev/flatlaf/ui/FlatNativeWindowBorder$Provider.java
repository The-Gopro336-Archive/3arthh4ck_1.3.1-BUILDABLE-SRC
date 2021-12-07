package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;
import javax.swing.event.ChangeListener;

public interface FlatNativeWindowBorder$Provider {
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
