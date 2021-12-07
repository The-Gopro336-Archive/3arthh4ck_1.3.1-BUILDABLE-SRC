/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiScreen
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class HModulesCommand
extends Command
implements Globals {
    public HModulesCommand() {
        super(new String[][]{{"hiddenmodule"}}, true);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            String name = args[1];
            Module module = (Module)Managers.MODULES.getObject(name);
            if (module != null) {
                if (Mouse.isButtonDown((int)1)) {
                    mc.displayGuiScreen((GuiScreen)new GuiChat(Commands.getPrefix() + module.getName() + " "));
                } else {
                    module.toggle();
                }
            } else {
                ChatUtil.sendMessage("\u00a7cAn error occurred.");
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        return PossibleInputs.empty();
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        completer.setMcComplete(true);
        return completer;
    }
}

