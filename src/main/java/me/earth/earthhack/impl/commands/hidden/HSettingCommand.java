/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.commands.hidden;

import java.util.ArrayList;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.hidden.HListSettingCommand;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class HSettingCommand
extends Command
implements Globals {
    public HSettingCommand() {
        super(new String[][]{{"hiddensetting"}, {"module"}, {"setting"}}, true);
    }

    @Override
    public void execute(String[] args) {
        Setting<?> setting;
        Module module;
        if (args.length > 2 && (module = (Module)Managers.MODULES.getObject(args[1])) != null && (setting = module.getSetting(args[2])) != null) {
            if (args.length == 3) {
                String command = HSettingCommand.getCommand(setting, module);
                Scheduler.getInstance().schedule(() -> mc.displayGuiScreen((GuiScreen)new GuiChat(command)));
            } else {
                HSettingCommand.update(setting, module, args, false);
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

    public static String getCommand(Setting<?> setting, Module module) {
        return Commands.getPrefix() + module.getName() + " \"" + setting.getName() + "\" ";
    }

    public static void update(Setting<?> setting, Module module, String[] args, boolean ignoreArgs) {
        if (setting.getName().equals("Enabled") && setting instanceof BooleanSetting) {
            if (ignoreArgs) {
                return;
            }
            boolean bool = Boolean.parseBoolean(args[3]);
            if (bool) {
                module.enable();
            } else {
                module.disable();
            }
            return;
        }
        ArrayList<String> settingNames = new ArrayList<String>(3 + module.getSettings().size());
        settingNames.add(module.getName() + "1");
        settingNames.add(module.getName() + "2");
        settingNames.add(module.getName() + "3");
        for (Setting<?> s : module.getSettings()) {
            settingNames.add(s.getName() + module.getName());
        }
        if (!ignoreArgs) {
            setting.fromString(args[3]);
        }
        if (module.getSettings().size() != settingNames.size() - 3) {
            settingNames.forEach(n -> Managers.CHAT.deleteMessage((String)n, 7000));
            Scheduler.getInstance().schedule(() -> Managers.COMMANDS.applyCommand(HListSettingCommand.create(module)));
        }
    }
}

