package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.event.ClickEvent;

class FontMod$1
extends SmartClickEvent {
    final String val$font;

    FontMod$1(ClickEvent.Action theAction, String string) {
        this.val$font = string;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "CustomFont Font \"" + this.val$font + "\"";
    }
}
