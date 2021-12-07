/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Cursor
 */
package org.newdawn.slick.gui;

import org.lwjgl.input.Cursor;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;

public interface GUIContext {
    public Input getInput();

    public long getTime();

    public int getScreenWidth();

    public int getScreenHeight();

    public int getWidth();

    public int getHeight();

    public Font getDefaultFont();

    public void setMouseCursor(String var1, int var2, int var3) throws SlickException;

    public void setMouseCursor(ImageData var1, int var2, int var3) throws SlickException;

    public void setMouseCursor(Cursor var1, int var2, int var3) throws SlickException;

    public void setDefaultMouseCursor();
}

