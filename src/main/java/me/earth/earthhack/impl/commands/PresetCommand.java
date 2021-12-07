package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.gui.ComponentBuilder;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public class PresetCommand
extends AbstractModuleCommand
implements Globals {
    public PresetCommand() {
        super((String[][])new String[][]{{"preset"}, {"module"}, {"preset"}}, 1);
        CommandDescriptions.register(this, "Apply only the best, carefully handpicked configs from the devs to modules.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cUse this command to apply a preset to a module.");
            return;
        }
        Module module = (Module)Managers.MODULES.getObject(args[1]);
        if (module == null) {
            ChatUtil.sendMessage("\u00a7cCould not find module \u00a7f" + args[1] + "\u00a7c" + ".");
            return;
        }
        ModuleData data = module.getData();
        if (data == null) {
            ChatUtil.sendMessage("\u00a7cThe module \u00a7f" + args[1] + "\u00a7c" + " has no Module-Data!");
            return;
        }
        if (args.length == 2) {
            boolean first = true;
            ComponentBuilder builder = new ComponentBuilder(args[1] + "-Presets: ");
            for (ModulePreset preset : data.getPresets()) {
                if (!first) {
                    builder.sibling("\u00a7f, \u00a7b").append();
                }
                first = false;
                builder.sibling("\u00a7b" + preset.getName()).addHover(preset.getDescription()).addSmartClickEvent("preset " + args[1] + " " + preset.getName()).append();
            }
            if (first) {
                ChatUtil.sendMessage("\u00a7cThe module \u00a7f" + args[1] + "\u00a7c" + " has no Presets.");
                return;
            }
            ChatUtil.sendComponent(builder.build());
        } else {
            ModulePreset result = null;
            for (ModulePreset preset : data.getPresets()) {
                if (!preset.getName().equalsIgnoreCase(args[2])) continue;
                result = preset;
                break;
            }
            if (result == null) {
                ChatUtil.sendMessage("\u00a7cThe module \u00a7f" + args[1] + "\u00a7c" + " doesn't have a " + "\u00a7b" + args[2] + "\u00a7c" + " preset.");
                return;
            }
            ModulePreset finalResult = result;
            GuiScreen before = PresetCommand.mc.currentScreen;
            Scheduler.getInstance().schedule(() -> mc.displayGuiScreen(new YesNoNonPausing((r, id) -> {
                mc.displayGuiScreen(before);
                if (!r) {
                    return;
                }
                ChatUtil.sendMessage("\u00a7aApplying preset \u00a7b" + finalResult.getName() + "\u00a7a" + " to " + "\u00a7f" + module.getName() + "\u00a7a" + ".");
                finalResult.apply();
            }, "\u00a7cApply preset \u00a7f" + finalResult.getName() + "\u00a7c" + " to module " + "\u00a7f" + module.getName() + "\u00a7c" + "?", "This will override your current settings for " + module.getName() + ".", 1337)));
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length > 2) {
            if (args.length == 3) {
                Module module = (Module)Managers.MODULES.getObject(args[1]);
                if (module == null) {
                    return PossibleInputs.empty();
                }
                ModuleData data = module.getData();
                if (data == null) {
                    return PossibleInputs.empty();
                }
                ModulePreset preset = CommandUtil.getNameableStartingWith(args[2], data.getPresets());
                if (preset != null) {
                    return new PossibleInputs(TextUtil.substring(preset.getName(), args[2].length()), "");
                }
            }
            return PossibleInputs.empty();
        }
        return super.getPossibleInputs(args);
    }
}
