package me.earth.earthhack.impl.util.helpers.addable.setting.component;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import net.minecraft.util.text.event.ClickEvent;

class SimpleRemovingComponent$1
extends SmartClickEvent {
    final SimpleRemovingSetting val$setting;

    SimpleRemovingComponent$1(ClickEvent.Action theAction, SimpleRemovingSetting simpleRemovingSetting) {
        this.val$setting = simpleRemovingSetting;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + ((Module)this.val$setting.getContainer()).getName() + " \"" + this.val$setting.getName() + "\" remove";
    }
}
