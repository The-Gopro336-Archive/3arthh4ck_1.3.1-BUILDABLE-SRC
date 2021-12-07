/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.gui.buttons.SimpleButton;
import net.minecraft.client.gui.GuiScreen;

public class EarthhackButton
extends SimpleButton
implements Globals {
    public EarthhackButton(int buttonID, int xPos, int yPos) {
        super(buttonID, xPos, yPos, 0, 40, 0, 60);
    }

    @Override
    public void onClick(GuiScreen parent, int id) {
        mc.displayGuiScreen((GuiScreen)new CommandGui(parent, id));
    }
}

