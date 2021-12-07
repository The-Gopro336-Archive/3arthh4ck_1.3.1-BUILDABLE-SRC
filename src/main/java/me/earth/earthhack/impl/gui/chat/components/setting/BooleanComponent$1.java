package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class BooleanComponent$1
extends SmartClickEvent {
    final BooleanSetting val$setting;

    BooleanComponent$1(ClickEvent.Action theAction, BooleanSetting booleanSetting) {
        this.val$setting = booleanSetting;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + ((Module)this.val$setting.getContainer()).getName() + " \"" + this.val$setting.getName() + "\" " + ((Boolean)this.val$setting.getValue() == false);
    }
}
