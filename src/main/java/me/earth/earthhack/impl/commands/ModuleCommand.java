package me.earth.earthhack.impl.commands;

import java.util.Optional;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.hidden.HListSettingCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class ModuleCommand
extends Command
implements Registrable {
    public ModuleCommand() {
        super(new String[][]{{"module"}, {"setting"}, {"value"}});
        CommandDescriptions.register(this, "Type only the name of the module to open the chatgui with its settings. You can also specify one of the modules settings and set it to a value.");
    }

    @Override
    public void onUnRegister() throws CantUnregisterException {
        throw new CantUnregisterException(this);
    }

    @Override
    public boolean fits(String[] args) {
        return args[0].length() > 0 && this.getModule(args[0]) != null;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            return;
        }
        Module module = (Module)Managers.MODULES.getObject(args[0]);
        if (module == null && (module = (Module)Managers.ELEMENTS.getObject(args[0])) == null) {
            module = this.getModule(args[0].toLowerCase());
            if (module == null) {
                ChatUtil.sendMessage("\u00a7cCould not find \u00a7f" + args[0] + "\u00a7c" + ". Try " + Commands.getPrefix() + "modules.");
            } else {
                ChatUtil.sendMessage("\u00a7cDid you mean \u00a7f" + module.getName() + "\u00a7c" + "?");
            }
            return;
        }
        if (module instanceof CustomCommandModule && ((CustomCommandModule)((Object)module)).execute(args)) {
            return;
        }
        if (args.length == 1) {
            Module finalModule = module;
            Scheduler.getInstance().schedule(() -> Managers.COMMANDS.applyCommand(HListSettingCommand.create(finalModule)));
            return;
        }
        Setting<?> setting = module.getSetting(args[1]);
        if (setting == null) {
            ChatUtil.sendMessage("\u00a7cCouldn't find setting \u00a7b" + args[1] + "\u00a7c" + " in " + "\u00a7f" + module.getName() + "\u00a7c" + ".");
            return;
        }
        if (args.length == 2) {
            ChatUtil.sendMessage("\u00a7cPlease specify a value for \u00a7f" + args[1] + "\u00a7c" + " in " + "\u00a7f" + module.getName() + "\u00a7c" + ".");
        } else {
            if (setting.getName().equals("Enabled")) {
                if (args[2].equalsIgnoreCase("true")) {
                    module.enable();
                } else if (args[2].equalsIgnoreCase("false")) {
                    module.disable();
                } else {
                    ChatUtil.sendMessage("\u00a7cPossible values: true or false!");
                    return;
                }
                Managers.CHAT.sendDeleteMessage("\u00a7l" + module.getName() + (module.isEnabled() ? "\u00a7a enabled." : "\u00a7c disabled."), module.getName(), 2000);
                return;
            }
            SettingResult result = setting.fromString(args[2]);
            if (!result.wasSuccessful()) {
                ChatUtil.sendMessage("\u00a7c" + result.getMessage());
            } else {
                String message = "<" + module.getDisplayName() + "> " + "\u00a7b" + setting.getName() + "\u00a7f" + " set to " + (setting.getValue() instanceof Boolean ? (((Boolean)setting.getValue()).booleanValue() ? "\u00a7a" : "\u00a7c") : "\u00a7b") + setting.toJson() + "\u00a7f" + ".";
                Managers.CHAT.sendDeleteMessage(message, setting.getName(), 3000);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs;
        Module module = this.getModule(args[0]);
        if (module == null) {
            return new PossibleInputs("", "\u00a7c not found");
        }
        if (module instanceof CustomCommandModule && ((CustomCommandModule)((Object)module)).getInput(args, inputs = PossibleInputs.empty())) {
            return inputs;
        }
        if (args.length == 1) {
            return new PossibleInputs(TextUtil.substring(module.getName(), args[0].length()), " <setting> <value>");
        }
        if (!args[0].equalsIgnoreCase(module.getName())) {
            return new PossibleInputs("", "\u00a7c not found");
        }
        Setting<?> setting = CommandUtil.getNameableStartingWith(args[1], module.getSettings());
        if (setting == null) {
            return new PossibleInputs("", "\u00a7c no such setting!");
        }
        if (args.length == 2) {
            return new PossibleInputs(TextUtil.substring(setting.getName(), args[1].length()), " " + setting.getInputs(null) + " <Current: " + setting.toJson() + ">" + (setting.getName().contains(" ") ? " <Surround setting with \"...\">" : ""));
        }
        if (args.length != 3) {
            return PossibleInputs.empty();
        }
        return new PossibleInputs(TextUtil.substring(setting.getInputs(args[2]), args[2].length()), " <Current: " + setting.toJson() + ">" + (setting.getName().contains(" ") ? " <Surround setting with \"...\">" : ""));
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        Module module;
        String[] args = completer.getArgs();
        if (args.length > 0 && (module = this.getModule(args[0])) instanceof CustomCommandModule) {
            switch (((CustomCommandModule)((Object)module)).complete(completer)) {
                case RETURN: {
                    return completer;
                }
                case SUPER: {
                    return super.onTabComplete(completer);
                }
            }
        }
        if (completer.isSame()) {
            module = this.getModule(args[0]);
            if (module == null) {
                return super.onTabComplete(completer);
            }
            if (args.length == 1) {
                return completer;
            }
            if (args.length == 2) {
                String[] custom;
                Setting<?> setting;
                boolean found;
                String[] custom2;
                Optional<Setting<?>> first = module.getSettings().stream().findFirst();
                if (!first.isPresent()) {
                    return completer;
                }
                if (module instanceof CustomCommandModule && (custom2 = ((CustomCommandModule)((Object)module)).getArgs()) != null && custom2.length > 0) {
                    found = false;
                    for (String s : custom2) {
                        if (found) {
                            completer.setResult(Commands.getPrefix() + args[0] + " " + (String)s);
                            return completer;
                        }
                        if (!args[1].equalsIgnoreCase(s)) continue;
                        found = true;
                    }
                }
                if ((setting = module.getSetting(args[1])) == null) {
                    completer.setResult(Commands.getPrefix() + args[0] + " " + this.getEscapedName(first.get().getName()));
                    return completer;
                }
                found = false;
                for (Setting setting2 : module.getSettings()) {
                    if (found) {
                        completer.setResult(Commands.getPrefix() + args[0] + " " + this.getEscapedName(setting2.getName()));
                        return completer;
                    }
                    if (!setting2.equals(setting)) continue;
                    found = true;
                }
                if (module instanceof CustomCommandModule && (custom = ((CustomCommandModule)((Object)module)).getArgs()) != null && custom.length > 0) {
                    completer.setResult(Commands.getPrefix() + args[0] + " " + custom[0]);
                    return completer;
                }
                completer.setResult(Commands.getPrefix() + args[0] + " " + this.getEscapedName(first.get().getName()));
            } else {
                Setting<?> setting;
                String[] custom;
                if (module instanceof CustomCommandModule && (custom = ((CustomCommandModule)((Object)module)).getArgs()) != null && custom.length > 0) {
                    for (String string : custom) {
                        if (!args[1].equalsIgnoreCase(string)) continue;
                        return completer;
                    }
                }
                if ((setting = module.getSetting(args[2])) != null) {
                    completer.setResult(Commands.getPrefix() + args[0] + " " + Completer.nextValueInSetting(setting, args[args.length - 1]));
                }
            }
            return completer;
        }
        if (args.length == 2) {
            module = this.getModule(args[0]);
            if (module == null) {
                return completer;
            }
            Setting<?> setting = CommandUtil.getNameableStartingWith(args[1], module.getSettings());
            if (setting == null) {
                return completer;
            }
            return completer.setResult(Commands.getPrefix() + args[0] + " " + this.getEscapedName(setting.getName()));
        }
        return super.onTabComplete(completer);
    }

    private String getEscapedName(String name) {
        return name.contains(" ") ? "\"" + name + "\"" : name;
    }

    private Module getModule(String name) {
        Module module = CommandUtil.getNameableStartingWith(name, Managers.MODULES);
        if (module != null) {
            return module;
        }
        return CommandUtil.getNameableStartingWith(name, Managers.ELEMENTS);
    }
}
