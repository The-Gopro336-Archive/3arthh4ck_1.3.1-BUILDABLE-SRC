package me.earth.earthhack.impl.commands.abstracts;

import java.util.Arrays;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.DelegateMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public abstract class AbstractMultiMacroCommand<T extends Macro>
extends Command
implements Globals {
    private final String ifSmallArgs;
    private final String macroName;

    public AbstractMultiMacroCommand(String[][] usage, String macroName, String ifSmallArgs) {
        super(usage, true);
        this.macroName = macroName;
        this.ifSmallArgs = ifSmallArgs;
    }

    protected abstract T getMacro(String var1, Bind var2, Macro ... var3);

    @Override
    public void execute(String[] args) {
        if (args.length <= 5) {
            ChatUtil.sendMessage("\u00a7c" + this.ifSmallArgs);
            return;
        }
        Macro[] macros = new Macro[args.length - 5];
        Macro[] realMacros = new Macro[args.length - 5];
        for (int i = 5; i < args.length; ++i) {
            Macro macro = (Macro)Managers.MACRO.getObject(args[i]);
            if (macro == null) {
                ChatUtil.sendMessage("\u00a7cCouldn't find macro: \u00a7f" + args[i] + "\u00a7c" + ".");
                return;
            }
            realMacros[i - 5] = macro;
            if (macro.getType() == MacroType.COMBINED || macro.getType() == MacroType.FLOW) {
                Earthhack.getLogger().info("Creating Delegate for Macro: " + macro.getName() + " : " + Arrays.toString(macro.getCommands()));
                String name = "CopyOf-" + macro.getName();
                while (Managers.MACRO.getObject(name) != null) {
                    name = name + "I";
                }
                DelegateMacro extraDelegate = DelegateMacro.delegate(name, macro);
                String name2 = "Delegate-" + macro.getName();
                while (Managers.MACRO.getObject(name2) != null) {
                    name2 = name2 + "I";
                }
                DelegateMacro delegate = new DelegateMacro(name2, extraDelegate.getName());
                try {
                    Managers.MACRO.register(extraDelegate);
                }
                catch (AlreadyRegisteredException e) {
                    ChatUtil.sendMessage("\u00a7cAn error occurred while delegating your macro: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                try {
                    Managers.MACRO.register(delegate);
                }
                catch (AlreadyRegisteredException e) {
                    ChatUtil.sendMessage("\u00a7cAn error occurred while delegating your macro: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                macros[i - 5] = delegate;
                continue;
            }
            macros[i - 5] = macro;
        }
        String name = args[2];
        String bind = args[3];
        Bind parsed = Bind.fromString(bind);
        T macro = this.getMacro(name, parsed, macros);
        StringBuilder conc = new StringBuilder();
        for (int i = 0; i < realMacros.length; ++i) {
            conc.append("\u00a7c").append(realMacros[i].getName());
            if (i == realMacros.length - 1) continue;
            conc.append("\u00a7f").append(", ");
        }
        String concatenated = conc.append("\u00a7f").toString();
        GuiScreen before = AbstractMultiMacroCommand.mc.currentScreen;
        Scheduler.getInstance().schedule(() -> mc.displayGuiScreen(new YesNoNonPausing((result, id) -> {
            mc.displayGuiScreen(before);
            if (!result) {
                this.registerMacro((Macro)macro, parsed, concatenated);
                return;
            }
            for (int i = 0; i < realMacros.length; ++i) {
                try {
                    Managers.MACRO.unregister(realMacros[i]);
                    continue;
                }
                catch (CantUnregisterException e) {
                    ChatUtil.sendMessage("\u00a7cA critical error occurred: \u00a7f" + realMacros[i].getName() + "\u00a7c" + " can't be deleted (" + e.getMessage() + ").");
                    e.printStackTrace();
                }
            }
            this.registerMacro((Macro)macro, parsed, concatenated);
        }, "", "Do you want to delete the macros " + concatenated + " ?", 1337)));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1) {
            return inputs;
        }
        inputs.setRest(" <macro> <macro> <...>");
        Macro macro = (Macro)CommandUtil.getNameableStartingWith(args[args.length - 1], Managers.MACRO.getRegistered());
        if (macro == null) {
            return inputs.setCompletion("").setRest("\u00a7c not found");
        }
        return inputs.setCompletion(TextUtil.substring(macro.getName(), args[args.length - 1].length()));
    }

    private void registerMacro(Macro macro, Bind parsed, String concatenated) {
        try {
            Managers.MACRO.register(macro);
            ChatUtil.sendMessage("\u00a7aAdded new " + this.macroName + ": " + "\u00a7f" + macro.getName() + " : " + "\u00a7b" + parsed.toString() + "\u00a7f" + " : " + "\u00a7c" + Commands.getPrefix() + concatenated + ".");
        }
        catch (AlreadyRegisteredException e) {
            ChatUtil.sendMessage("\u00a7cCouldn't add Macro \u00a7f" + macro.getName() + "\u00a7c" + ", a Macro with that name already exists.");
        }
    }
}
