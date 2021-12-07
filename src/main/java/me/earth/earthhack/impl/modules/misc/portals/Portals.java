/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.portals;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.misc.portals.ListenerTeleport;
import me.earth.earthhack.impl.modules.misc.portals.PortalsData;

public class Portals
extends Module {
    protected final Setting<Boolean> godMode = this.register(new BooleanSetting("GodMode", false));

    public Portals() {
        super("Portals", Category.Misc);
        this.listeners.add(new ListenerTeleport(this));
        this.register(new BooleanSetting("Chat", true));
        this.setData(new PortalsData(this));
    }

    @Override
    public String getDisplayInfo() {
        if (this.godMode.getValue().booleanValue()) {
            return "\u00a7cGodMode";
        }
        return null;
    }
}

