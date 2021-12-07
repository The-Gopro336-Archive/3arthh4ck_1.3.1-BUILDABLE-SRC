package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class EnumComponent$1
extends SmartClickEvent {
    final EnumSetting val$setting;

    EnumComponent$1(ClickEvent.Action theAction, EnumSetting enumSetting) {
        this.val$setting = enumSetting;
        super(theAction);
    }

    @Override
    public String getValue() {
        Enum<?> next = EnumHelper.next((Enum)this.val$setting.getValue());
        return Commands.getPrefix() + "hiddensetting " + ((Module)this.val$setting.getContainer()).getName() + " \"" + this.val$setting.getName() + "\" " + next.name();
    }
}
