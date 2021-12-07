package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.render.waypoints.WayPointSetting;
import net.minecraft.util.text.event.ClickEvent;

class WayPointComponent$1
extends SmartClickEvent {
    final WayPointSetting val$setting;

    WayPointComponent$1(ClickEvent.Action theAction, WayPointSetting wayPointSetting) {
        this.val$setting = wayPointSetting;
        super(theAction);
    }

    @Override
    public String getValue() {
        return Commands.getPrefix() + "hiddensetting " + ((Module)this.val$setting.getContainer()).getName() + " \"" + this.val$setting.getName() + "\" remove";
    }
}
