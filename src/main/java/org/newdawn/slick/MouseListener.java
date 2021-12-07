/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.ControlledInputReciever;

public interface MouseListener
extends ControlledInputReciever {
    public void mouseWheelMoved(int var1);

    public void mouseClicked(int var1, int var2, int var3, int var4);

    public void mousePressed(int var1, int var2, int var3);

    public void mouseReleased(int var1, int var2, int var3);

    public void mouseMoved(int var1, int var2, int var3, int var4);

    public void mouseDragged(int var1, int var2, int var3, int var4);
}

