/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 */
package me.earth.earthhack.impl.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.abstracts.AbstractMultiMacroCommand;
import me.earth.earthhack.impl.commands.hidden.HMacroCombineCommand;
import me.earth.earthhack.impl.commands.hidden.HMacroFlowCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

public class MacroCommand
extends Command {
    private static final BindSetting BIND_INSTANCE = new BindSetting("Bind");
    private final List<AbstractMultiMacroCommand<?>> custom = new ArrayList();

    public MacroCommand() {
        super(new String[][]{{"macro"}, {"add", "del", "release", "use"}, {"name"}, {"bind", "release"}, {"flow", "combine", "command"}});
        CommandDescriptions.register(this, "Manage your Macros. Use \u00a7lflow\u00a7r to create a macro that switches between the given macros everytime its used. Use \u00a7lcombine\u00a7r to combine multiple macros into one. You can also use these features to combine or flow macros even further customizing your macros to the maximum.\u00a7l Release\u00a7r <true/false> allows you to make macros that toggle when you release a key.");
        this.custom.add(new HMacroCombineCommand());
        this.custom.add(new HMacroFlowCommand());
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            TextComponentString component = new TextComponentString("Macros: ");
            Iterator iterator = Managers.MACRO.getRegistered().stream().filter(m -> m.getType() != MacroType.DELEGATE).collect(Collectors.toList()).iterator();
            while (iterator.hasNext()) {
                Macro macro = (Macro)iterator.next();
                TextComponentString macroComp = new TextComponentString("\u00a7b" + macro.getName());
                macroComp.setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("Bind: \u00a7b" + macro.getBind().toString() + "\u00a7f" + ", Command: " + "\u00a7c" + Arrays.toString(macro.getCommands()))))));
                component.appendSibling((ITextComponent)macroComp);
                if (!iterator.hasNext()) continue;
                component.appendSibling((ITextComponent)new TextComponentString("\u00a7f, "));
            }
            Managers.CHAT.sendDeleteComponent((ITextComponent)component, "Macros", 3000);
            return;
        }
        if (args.length == 2) {
            ChatUtil.sendMessage("\u00a7cPlease Specify a Macro");
            return;
        }
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("use")) {
                this.executeMacro(args[2]);
                return;
            }
            if (args[1].equalsIgnoreCase("release")) {
                Macro m2 = (Macro)Managers.MACRO.getObject(args[2]);
                if (m2 == null) {
                    ChatUtil.sendMessage("\u00a7cMacro \u00a7f" + args[2] + "\u00a7c" + " doesn't exist.");
                } else if (args.length == 3) {
                    boolean bl = m2.isRelease();
                    ChatUtil.sendMessage("\u00a7aMacro \u00a7b" + args[2] + "\u00a7a" + (bl ? " toggles" : " doesn't toggle") + " on release.");
                } else {
                    boolean bl = Boolean.parseBoolean(args[3]);
                    m2.setRelease(bl);
                    ChatUtil.sendMessage("\u00a7aMacro \u00a7b" + args[2] + "\u00a7a" + " now" + (bl ? " toggles " : " doesn't toggle ") + "on releasing the key.");
                }
                return;
            }
        }
        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("del")) {
                this.delMacro(args);
            } else if (args[1].equalsIgnoreCase("add")) {
                ChatUtil.sendMessage("\u00a7cPlease specify a bind and command.");
            } else {
                this.onInvalidInput(args);
            }
        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("del")) {
                this.delMacro(args);
            } else if (args[1].equalsIgnoreCase("add")) {
                ChatUtil.sendMessage("\u00a7cPlease specify a command.");
            } else {
                this.onInvalidInput(args);
            }
        } else if (args[1].equalsIgnoreCase("del")) {
            this.delMacro(args);
        } else if (args[1].equalsIgnoreCase("add")) {
            for (Command command : this.custom) {
                if (!command.fits(Arrays.copyOfRange(args, 4, args.length))) continue;
                command.execute(args);
                return;
            }
            String name = args[2];
            String string = args[3];
            String comm = CommandUtil.concatenate(args, 4);
            Bind parsed = Bind.fromString(string);
            Macro macro = new Macro(name, parsed, new String[]{comm});
            try {
                Managers.MACRO.register(macro);
                ChatUtil.sendMessage("\u00a7aAdded new Macro: \u00a7f" + macro.getName() + " : " + "\u00a7b" + parsed + "\u00a7f" + " : " + "\u00a7c" + Commands.getPrefix() + comm);
            }
            catch (AlreadyRegisteredException e) {
                ChatUtil.sendMessage("\u00a7cCouldn't add Macro \u00a7f" + macro.getName() + "\u00a7c" + ", a Macro with that name already exists.");
            }
        } else {
            this.onInvalidInput(args);
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length < 3) {
            return inputs;
        }
        if (args.length == 3) {
            Macro macro = this.getMacroStartingWith(args[2]);
            if ((args[1].equalsIgnoreCase("use") || args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("release")) && macro == null) {
                return inputs.setCompletion("").setRest("\u00a7c not found");
            }
            if (args[1].equalsIgnoreCase("add") && macro != null) {
                return inputs.setCompletion(TextUtil.substring(macro.getName(), args[2].length())).setRest("\u00a7c <Macro: \u00a7f" + macro.getName() + "\u00a7c" + "> already exists.");
            }
            if (macro != null) {
                inputs.setCompletion(TextUtil.substring(macro.getName(), args[2].length()));
                if (args[1].equalsIgnoreCase("release")) {
                    return inputs.setRest(" <true/false>");
                }
                return inputs.setRest("");
            }
            return inputs.setCompletion("").setRest(" <bind> <flow/combine/command>");
        }
        if (args.length == 4) {
            if (args[1].equalsIgnoreCase("release")) {
                String s = CommandUtil.completeBoolean(args[3]);
                if (s == null) {
                    return inputs.setCompletion("").setRest("\u00a7c try true/false");
                }
                return inputs.setCompletion(s).setRest("");
            }
            if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("use")) {
                return PossibleInputs.empty();
            }
            return inputs.setCompletion(TextUtil.substring(BIND_INSTANCE.getInputs(args[3]), args[3].length())).setRest(" <flow/combine/command>");
        }
        if (args[2].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("use") || args[1].equalsIgnoreCase("release")) {
            return PossibleInputs.empty();
        }
        String[] arguments = Arrays.copyOfRange(args, 4, args.length);
        for (Command command : this.custom) {
            if (!command.fits(arguments)) continue;
            return command.getPossibleInputs(arguments);
        }
        Command target = Managers.COMMANDS.getCommandForMessage(arguments);
        if (target == null) {
            return PossibleInputs.empty();
        }
        return target.getPossibleInputs(arguments);
    }

    private void onInvalidInput(String[] args) {
        Macro macro = this.getMacroStartingWith(args[2]);
        if (macro == null) {
            Earthhack.getLogger().warn(Arrays.toString(args));
            ChatUtil.sendMessage("\u00a7cUsage is <add/del>.");
        } else {
            ChatUtil.sendMessage("\u00a7cBad Input, info about \u00a7f" + macro.getName() + "\u00a7c" + ": " + "\u00a7f" + "<" + "\u00a7b" + "bind: " + macro.getBind().toString() + "\u00a7f" + "> <" + "\u00a7b" + "commands: " + Arrays.toString(macro.getCommands()) + "\u00a7f" + ">");
        }
    }

    private void delMacro(String[] args) {
        Macro macro = this.getMacroStartingWith(args[2]);
        if (macro == null) {
            ChatUtil.sendMessage("\u00a7cCouldn't find macro " + args[2] + ".");
            return;
        }
        if (macro.getName().equalsIgnoreCase(args[2])) {
            try {
                Managers.MACRO.unregister(macro);
                ChatUtil.sendMessage("Removed Macro \u00a7c" + args[2] + "\u00a7f" + ".");
            }
            catch (CantUnregisterException e) {
                ChatUtil.sendMessage("Could not unregister Macro \u00a7c" + args[2] + "\u00a7f" + ".");
            }
        } else {
            ChatUtil.sendMessage("\u00a7cCouldn't find " + args[2] + " did you mean " + "\u00a7b" + macro.getName() + "\u00a7c" + "?");
        }
    }

    private void executeMacro(String name) {
        Macro macro = (Macro)Managers.MACRO.getObject(name);
        if (macro == null) {
            ChatUtil.sendMessage("\u00a7cMacro \u00a7f" + name + "\u00a7c" + " couldn't be found!");
            return;
        }
        if (Managers.MACRO.isSafe()) {
            macro.execute(Managers.COMMANDS);
        } else {
            try {
                macro.execute(Managers.COMMANDS);
            }
            catch (Throwable t) {
                ChatUtil.sendMessage("\u00a7cAn error occurred while executing macro \u00a7f" + name + "\u00a7c" + ": " + (t.getMessage() == null ? t.getClass().getName() : t.getMessage()));
            }
        }
    }

    private Macro getMacroStartingWith(String name) {
        return (Macro)CommandUtil.getNameableStartingWith(name, Managers.MACRO.getRegistered().stream().filter(m -> m.getType() != MacroType.DELEGATE).collect(Collectors.toList()));
    }
}

