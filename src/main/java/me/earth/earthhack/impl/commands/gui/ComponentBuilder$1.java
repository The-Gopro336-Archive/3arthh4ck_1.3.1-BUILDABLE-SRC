package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class ComponentBuilder$1
extends SmartClickEvent {
    final String val$command;

    ComponentBuilder$1(ClickEvent.Action theAction, String string) {
        this.val$command = string;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + this.val$command;
    }
}
