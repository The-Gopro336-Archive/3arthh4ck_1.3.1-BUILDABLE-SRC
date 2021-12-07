package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

final class ModuleListCommand$1
extends SmartClickEvent {
    final Module val$module;

    ModuleListCommand$1(ClickEvent.Action theAction, Module module) {
        this.val$module = module;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "toggle " + this.val$module.getName();
    }
}
