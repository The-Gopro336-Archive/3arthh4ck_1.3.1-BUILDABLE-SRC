package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class BindCommand
extends AbstractModuleCommand {
    private static final BindSetting BIND = new BindSetting("", Bind.none());

    public BindCommand() {
        super("bind", new String[][]{{"bind"}});
        CommandDescriptions.register(this, "Sets the binds of modules.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7c Please specify a module!");
            return;
        }
        if (args.length == 2) {
            ChatUtil.sendMessage("\u00a7c Please specify a bind!");
            return;
        }
        Module module = (Module)Managers.MODULES.getObject(args[1]);
        if (module == null) {
            ChatUtil.sendMessage("\u00a7cModule \u00a7f" + args[1] + "\u00a7c" + " not found!");
            return;
        }
        Setting<?> bind = module.getSetting("Bind");
        if (bind == null) {
            ChatUtil.sendMessage("\u00a7c" + module.getName() + " can't be bound.");
            return;
        }
        bind.fromString(args[2]);
        ChatUtil.sendMessage(module.getName() + "\u00a7a" + " bound to " + module.getBind().toString());
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length == 3) {
            return new PossibleInputs(TextUtil.substring(BIND.getInputs(args[2]), args[2].length()), "");
        }
        return super.getPossibleInputs(args);
    }
}
