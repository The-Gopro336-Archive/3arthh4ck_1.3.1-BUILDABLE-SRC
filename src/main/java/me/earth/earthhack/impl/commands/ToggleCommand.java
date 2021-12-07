/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class ToggleCommand
extends AbstractModuleCommand {
    public ToggleCommand() {
        super((String[][])new String[][]{{"toggle"}, {"module"}, {"times"}}, 1);
        CommandDescriptions.register(this, "Toggle the specified module. If you specify a number you can toggle it multiple times. This can be useful to set the FakePlayer to another position for example (t Fakeplayer 2).");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            Module module = (Module)Managers.MODULES.getObject(args[1]);
            if (module != null) {
                int times = 1;
                if (args.length > 2) {
                    try {
                        times = Integer.parseInt(args[2]);
                    }
                    catch (NumberFormatException ignored) {
                        ChatUtil.sendMessage("<ToggleCommand> \u00a7c" + args[2] + " is not a valid number.");
                        return;
                    }
                }
                String color = module.isEnabled() && times % 2 == 0 || !module.isEnabled() && times % 2 != 0 ? "\u00a7a" : "\u00a7c";
                Managers.CHAT.sendDeleteMessage(color + "Toggling " + "\u00a7f" + "\u00a7l" + module.getDisplayName() + color + (times > 1 ? " " + times + "x." : "."), module.getName(), 2000);
                int finalTimes = times;
                Scheduler.getInstance().schedule(() -> {
                    for (int i = 0; i < finalTimes; ++i) {
                        module.toggle();
                    }
                });
            } else {
                ChatUtil.sendMessage("<ToggleCommand> \u00a7cCouldn't find " + args[1] + ".");
            }
            return;
        }
        ChatUtil.sendMessage("<ToggleCommand> \u00a7cUsage is: " + this.getFullUsage());
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > 1) {
            Module module = this.getModule(args, 1);
            if (module != null) {
                String enabled;
                String string = enabled = module.isEnabled() ? " <Currently: Enabled>" : " <Currently: Disabled>";
                if (args.length > 2) {
                    try {
                        int times = Integer.parseInt(args[2]);
                        return inputs.setCompletion("").setRest(enabled + " <Will be:" + (module.isEnabled() && times % 2 == 0 || !module.isEnabled() && times % 2 != 0 ? "\u00a7a Enabled" : "\u00a7c Disabled") + "\u00a7f" + ">");
                    }
                    catch (NumberFormatException e) {
                        return inputs.setCompletion("").setRest("\u00a7c <" + args[2] + " is not a number>");
                    }
                }
                return inputs.setCompletion(TextUtil.substring(module.getName(), args[1].length())).setRest(" <times> " + enabled);
            }
            return inputs.setCompletion("").setRest("\u00a7c not found");
        }
        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        Module module;
        if (completer.getArgs().length == 1) {
            completer.setResult(Commands.getPrefix() + "toggle");
            return completer;
        }
        if (completer.getArgs().length == 2 && (module = (Module)CommandUtil.getNameableStartingWith(completer.getArgs()[1], Managers.MODULES.getRegistered())) != null) {
            completer.setResult(Commands.getPrefix() + completer.getArgs()[0] + " " + module.getName());
        }
        return completer;
    }
}

