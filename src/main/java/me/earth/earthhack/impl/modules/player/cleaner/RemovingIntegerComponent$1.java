package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingInteger;
import net.minecraft.util.text.event.ClickEvent;

class RemovingIntegerComponent$1
extends SmartClickEvent {
    final Module val$module;
    final RemovingInteger val$setting;

    RemovingIntegerComponent$1(ClickEvent.Action theAction, Module module, RemovingInteger removingInteger) {
        this.val$module = module;
        this.val$setting = removingInteger;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + this.val$module.getName() + " \"" + this.val$setting.getName() + "\" remove";
    }
}
