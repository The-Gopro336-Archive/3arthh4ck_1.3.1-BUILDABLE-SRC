/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 */
package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

public class HListSettingCommand
extends Command
implements Globals,
CommandScheduler {
    public HListSettingCommand() {
        super(new String[][]{{"hiddenlistsetting"}, {"module"}}, true);
    }

    @Override
    public void execute(String[] args) {
        Module module;
        if (args.length > 1 && (module = (Module)Managers.MODULES.getObject(args[1])) != null) {
            HListSettingCommand.sendSettings(module);
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

    private static void sendSettings(Module module) {
        Managers.CHAT.sendDeleteMessage(" ", module.getName() + "1", 7000);
        Managers.CHAT.sendDeleteComponent(new TextComponentString(module.getName() + " : " + "\u00a77" + module.getCategory().toString()).setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(module.getData().getDescription()))))), module.getName() + "2", 7000);
        int i = 2;
        for (Setting<?> setting : module.getSettings()) {
            SettingComponent component = ComponentFactory.create(setting);
            Managers.CHAT.sendDeleteComponent((ITextComponent)component, setting.getName() + module.getName(), 7000);
        }
        Managers.CHAT.sendDeleteMessage(" ", module.getName() + "3", 7000);
        Scheduler.getInstance().schedule(() -> mc.displayGuiScreen((GuiScreen)new GuiChat()));
        SCHEDULER.submit(() -> mc.addScheduledTask(() -> {
            if (HListSettingCommand.mc.ingameGUI != null) {
                HListSettingCommand.mc.ingameGUI.getChatGUI().scroll(1);
            }
        }), 100);
    }

    public static String create(Module module) {
        return Commands.getPrefix() + "hiddenlistsetting " + module.getName();
    }
}

