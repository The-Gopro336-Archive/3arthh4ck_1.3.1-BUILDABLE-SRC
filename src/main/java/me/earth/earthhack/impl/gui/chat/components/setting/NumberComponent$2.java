package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class NumberComponent$2
extends SmartClickEvent {
    final Module val$module;
    final NumberSetting val$setting;

    NumberComponent$2(ClickEvent.Action theAction, Module module, NumberSetting numberSetting) {
        this.val$module = module;
        this.val$setting = numberSetting;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + this.val$module.getName() + " \"" + this.val$setting.getName() + "\" " + NumberComponent.this.getNewValue(false);
    }
}
