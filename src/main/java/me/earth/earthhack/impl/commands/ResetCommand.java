/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.commands;

import java.util.ArrayList;
import java.util.Iterator;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public class ResetCommand
extends AbstractModuleCommand
implements Globals {
    public ResetCommand() {
        super((String[][])new String[][]{{"reset"}, {"module"}, {"setting"}}, 1);
        CommandDescriptions.register(this, "Resets all settings of the module to their default values.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.sendMessage("Use this command to reset Modules and Settings.");
            return;
        }
        Module module = (Module)Managers.MODULES.getObject(args[1]);
        if (module == null) {
            ChatUtil.sendMessage("\u00a7cModule \u00a7f" + args[1] + "\u00a7c" + " not found!");
            return;
        }
        if (args.length == 2) {
            Scheduler.getInstance().schedule(() -> {
                GuiScreen previous = ResetCommand.mc.currentScreen;
                mc.displayGuiScreen((GuiScreen)new YesNoNonPausing((result, id) -> {
                    mc.displayGuiScreen(previous);
                    if (!result) {
                        return;
                    }
                    for (Setting<?> setting : module.getSettings()) {
                        setting.reset();
                    }
                    ChatUtil.sendMessage("\u00a7aModule \u00a7f" + module.getName() + "\u00a7a" + " has been reset.");
                }, "Do you really want to reset the Module " + module.getName() + "?", "", 1337));
            });
        } else {
            ArrayList settings = new ArrayList(args.length - 2);
            for (int i = 2; i < args.length; ++i) {
                Setting<?> setting = module.getSetting(args[i]);
                if (setting != null) {
                    settings.add(setting);
                    continue;
                }
                ChatUtil.sendMessage("\u00a7cCould not find Setting \u00a7f" + args[i] + "\u00a7c" + " in module " + "\u00a7f" + module.getName() + "\u00a7c" + ".");
            }
            if (settings.isEmpty()) {
                return;
            }
            StringBuilder settingString = new StringBuilder("\u00a7c");
            settingString.append("Do you really want to reset the Setting");
            if (settings.size() > 1) {
                settingString.append("s ");
            } else {
                settingString.append(" ");
            }
            settingString.append("\u00a7f");
            Iterator itr = settings.iterator();
            while (itr.hasNext()) {
                Setting s = (Setting)itr.next();
                settingString.append(s.getName());
                if (!itr.hasNext()) continue;
                settingString.append("\u00a7c").append(", ").append("\u00a7f");
            }
            settingString.append("\u00a7c").append(" in the module ").append("\u00a7f").append(module.getName()).append("\u00a7c").append("?");
            Scheduler.getInstance().schedule(() -> {
                GuiScreen previous = ResetCommand.mc.currentScreen;
                mc.displayGuiScreen((GuiScreen)new YesNoNonPausing((result, id) -> {
                    mc.displayGuiScreen(previous);
                    if (!result) {
                        return;
                    }
                    for (Setting setting : settings) {
                        setting.reset();
                        ChatUtil.sendMessage("\u00a7cReset " + module.getName() + "\u00a7c" + " - " + "\u00a7f" + setting.getName() + "\u00a7c" + ".");
                    }
                }, settingString.toString(), "", 1337));
            });
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > 2) {
            Module module = (Module)Managers.MODULES.getObject(args[1]);
            if (module == null) {
                return inputs.setCompletion("").setRest("\u00a7c not found.");
            }
            Setting<?> s = CommandUtil.getNameableStartingWith(args[args.length - 1], module.getSettings());
            if (s == null) {
                return inputs.setCompletion("").setRest("\u00a7c not found.");
            }
            return inputs.setCompletion(TextUtil.substring(s.getName(), args[args.length - 1].length()));
        }
        return inputs;
    }
}

