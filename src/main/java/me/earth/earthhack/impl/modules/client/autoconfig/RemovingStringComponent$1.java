package me.earth.earthhack.impl.modules.client.autoconfig;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.autoconfig.RemovingString;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class RemovingStringComponent$1
extends SmartClickEvent {
    final Module val$module;
    final RemovingString val$setting;

    RemovingStringComponent$1(ClickEvent.Action theAction, Module module, RemovingString removingString) {
        this.val$module = module;
        this.val$setting = removingString;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + this.val$module.getName() + " \"" + this.val$setting.getName() + "\" remove";
    }
}
