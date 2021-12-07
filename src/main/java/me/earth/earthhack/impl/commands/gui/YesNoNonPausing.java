/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiYesNo
 *  net.minecraft.client.gui.GuiYesNoCallback
 */
package me.earth.earthhack.impl.commands.gui;

import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

public class YesNoNonPausing
extends GuiYesNo {
    public YesNoNonPausing(GuiYesNoCallback parentScreenIn, String messageLine1In, String messageLine2In, int parentButtonClickedIdIn) {
        super(parentScreenIn, messageLine1In, messageLine2In, parentButtonClickedIdIn);
    }

    public YesNoNonPausing(GuiYesNoCallback parentScreenIn, String messageLine1In, String messageLine2In, String confirmButtonTextIn, String cancelButtonTextIn, int parentButtonClickedIdIn) {
        super(parentScreenIn, messageLine1In, messageLine2In, confirmButtonTextIn, cancelButtonTextIn, parentButtonClickedIdIn);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

