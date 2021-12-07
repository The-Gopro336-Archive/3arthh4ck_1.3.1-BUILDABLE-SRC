/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.ControlledInputReciever;

public interface ControllerListener
extends ControlledInputReciever {
    public void controllerLeftPressed(int var1);

    public void controllerLeftReleased(int var1);

    public void controllerRightPressed(int var1);

    public void controllerRightReleased(int var1);

    public void controllerUpPressed(int var1);

    public void controllerUpReleased(int var1);

    public void controllerDownPressed(int var1);

    public void controllerDownReleased(int var1);

    public void controllerButtonPressed(int var1, int var2);

    public void controllerButtonReleased(int var1, int var2);
}

